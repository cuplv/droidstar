package edu.colorado.plv.droidstar.experiments.lp;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.UUID;

import java.lang.reflect.Method;

import java.lang.AssertionError;

import android.os.Handler.Callback;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import edu.colorado.plv.droidstar.LearningPurpose;
import static edu.colorado.plv.droidstar.Static.*;

public class BluetoothAdapterLP extends LearningPurpose {
    protected BluetoothAdapter ba;
    protected Boolean started = false;
    protected BluetoothSocket sock;
    protected Context connectionContext;

    public BluetoothAdapterLP(Context c) {
        super(c);
        this.connectionContext = c;
        this.ba = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        c.registerReceiver(receiver, filter);
    }

    // INPUTS
    public static String ENABLE = "enable";
    public static String DISABLE = "disable";
    public static String CONNECT = "connect";
    public static String DISCONNECT = "disconnect";

    // OUTPUTS
    public static String ERROR = "error";
    public static String UPDATE_ON = "u_on";
    public static String UPDATE_OFF = "u_off";
    public static String UPDATE_CONN = "u_conn";
    public static String UPDATE_DISCONN = "u_disconn";

    @Override
    protected String resetActions(Context ctx, Callback cb) {
        if (started) {
            if (sock != null) {
                try{sock.close();}catch(Exception e){}
            }
            ba.enable();
            ba.disable();
            return UPDATE_OFF;

            // if (ba.isEnabled()) {
            //     ba.disable();
            //     return UPDATE_OFF;
            // } else {
            //     return null;
            // }

        } else {
            started = true;
            return null;
        }
    }

    @Override
    protected List<String> uniqueInputSet() {
        List<String>is = new ArrayList();
        is.add(ENABLE);
        is.add(DISABLE);
        is.add(CONNECT);
        return is;
    }

    @Override
    public List<String> singleInputs() {return uniqueInputSet();}

    @Override
    public int eqLength() {return 2;}

    @Override
    public int betaTimeout() {return 2000;}

    // Necessary to handle dangling disconnection callbacks
    @Override
    public int safetyTimeout() {return 5000;}

    @Override
    public boolean isError(String o) {return o.equals(ERROR);}

    @Override
    public String shortName() {return "BluetoothAdapter";}

    @Override
    public void giveInput(String input, int altKey) throws Exception {
        if (input.equals(ENABLE)) {
            if (! ba.enable()) {
                throw new Exception(ENABLE + " [failed]");
            }
        } else if (input.equals(DISABLE)) {
            if (! ba.disable()) {
                throw new Exception(DISABLE + " [failed]");
            }
        } else if (input.equals(CONNECT)) {
            doConnect();
        } else {
            throw new AssertionError("Unrecognized Input!");
        }
    }

    protected void doConnect() throws Exception {
        BluetoothDevice dv = ba.getRemoteDevice(MyMAC);
        Method m = dv.getClass().getMethod
            ("createRfcommSocket", new Class[] {int.class});
        sock = (BluetoothSocket) m.invoke(dv,3);
        ba.cancelDiscovery();
        sock.connect();
        sock.close();
    }

    public static String MyMAC = "E4:A4:71:9C:14:12";
    public static String MyUUID = "5c44f03a-34fd-11e7-a919-92ebcb67fe33";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                final String action = i.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = i.getIntExtra
                        (BluetoothAdapter.EXTRA_STATE,
                         BluetoothAdapter.ERROR);

                    switch(state) {
                    case BluetoothAdapter.STATE_ON:
                        respond(UPDATE_ON);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        respond(UPDATE_OFF);
                        break;
                    }
                } else if (action.equals
                           (BluetoothDevice.ACTION_ACL_CONNECTED)) {
                    respond(UPDATE_CONN);
                } else if (action.equals(BluetoothAdapter.ERROR)) {
                }
            }
        };
}
