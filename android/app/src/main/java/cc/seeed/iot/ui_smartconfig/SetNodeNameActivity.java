package cc.seeed.iot.ui_smartconfig;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cc.seeed.iot.MyApplication;
import cc.seeed.iot.R;
import cc.seeed.iot.datastruct.User;
import cc.seeed.iot.udp.ConfigNodeData;
import cc.seeed.iot.udp.ConfigUdpSocket;
import cc.seeed.iot.ui_main.MainScreenActivity;
import cc.seeed.iot.webapi.IotApi;
import cc.seeed.iot.webapi.IotService;
import cc.seeed.iot.webapi.model.Node;
import cc.seeed.iot.webapi.model.NodeResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SetNodeNameActivity extends AppCompatActivity {
    public static final String NODE_LOCAL_IP_ADDRESS = "node.local.ip.address";
    public Toolbar mToolbar;
    public EditText mNodeNameView;
    public Button mGoPlayButtonView;
    private ConfigUdpSocket udpClient;
    private Intent intent;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smartconfig_connected);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        intent = getIntent();
        ip = intent.getStringExtra(NODE_LOCAL_IP_ADDRESS);

        udpClient = new ConfigUdpSocket();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("WIFI Iot Node");

        mNodeNameView = (EditText) findViewById(R.id.add_node_name);

        mGoPlayButtonView = (Button) findViewById(R.id.first_time_how_to_api_key);
        mGoPlayButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String node_name = mNodeNameView.getText().toString();
                attemptLogin(node_name);
            }
        });

    }

    private void GoSetNodeSn(Node node) {

        if (node == null)
            return;

        String cmd_set_sn = "KeySN: " + node.node_key + "," + node.node_sn;

        Log.i("iot", "cmd_sn: " + cmd_set_sn);
        Log.i("iot", "ip: " + ip);

        new SetNodeSn().execute(cmd_set_sn, ip);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void attemptLogin(final String node_name) {
        final Node node = new Node();
        boolean cancel = false;
        View focusView = null;

        final ProgressDialog mProgressBar = new ProgressDialog(this);

        if (cancel) {
            focusView.requestFocus();
        } else {
//            showProgress(true);
            mProgressBar.setTitle("connect server...");
            mProgressBar.show();
            IotApi api = new IotApi();
            User user = ((MyApplication) getApplication()).getUser();
            api.setAccessToken(user.user_key);
            IotService iot = api.getService();
            iot.nodesCreate(node_name, new Callback<NodeResponse>() {
                @Override
                public void success(NodeResponse nodeResponse, Response response) {
                    String status = nodeResponse.status;
                    if (status.equals("200")) {
                        mProgressBar.dismiss();

                        node.name = node_name;
                        node.node_key = nodeResponse.node_key;
                        node.node_sn = nodeResponse.node_sn;

                        GoSetNodeSn(node);
                    } else {
//                        showProgress(false);
                        mProgressBar.dismiss();
                        mNodeNameView.setError(nodeResponse.msg);
                        mNodeNameView.requestFocus();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
//                    showProgress(false);
                    mProgressBar.dismiss();
                    Toast.makeText(SetNodeNameActivity.this, "Connect server error!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private class SetNodeSn extends AsyncTask<String, Void, Boolean> {
        //todo: real-time refresh
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(SetNodeNameActivity.this);
//            mProgressDialog.setMessage("search node...");
            mProgressDialog.setTitle("Set node_sn...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String cmd = params[0];
            String ipAddr = params[1];
            udpClient.setSoTimeout(1000); //1s timeout
            udpClient.sendData(cmd, ipAddr);
            for (int i = 0; i < 3; i++) {
                try {
                    byte[] bytes = udpClient.receiveData();
                    if (new String(bytes).substring(0, 1 + 1).equals("ok")) {
                        Log.e("iot", "set Node Success");
                        //todo: upda ui is success
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    udpClient.setSoTimeout(3000);
                    udpClient.sendData(cmd, ipAddr);
                    continue;
                } catch (IOException e) {
                    Log.e("iot", "Error[AsyIO]:" + e);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            mProgressDialog.dismiss();
            Intent intent = new Intent(SetNodeNameActivity.this, MainScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
