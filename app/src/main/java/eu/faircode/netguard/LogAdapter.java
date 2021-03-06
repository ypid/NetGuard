package eu.faircode.netguard;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class LogAdapter extends CursorAdapter {
    private int colTime;
    private int colVersion;
    private int colIP;
    private int colProtocol;
    private int colPort;
    private int colFlags;
    private int colUid;
    private int colConnection;
    private int colInteractive;

    public LogAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        colTime = cursor.getColumnIndex("time");
        colVersion = cursor.getColumnIndex("version");
        colIP = cursor.getColumnIndex("ip");
        colProtocol = cursor.getColumnIndex("protocol");
        colPort = cursor.getColumnIndex("port");
        colFlags = cursor.getColumnIndex("flags");
        colUid = cursor.getColumnIndex("uid");
        colConnection = cursor.getColumnIndex("connection");
        colInteractive = cursor.getColumnIndex("interactive");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.log, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Get values
        long time = cursor.getLong(colTime);
        int version = (cursor.isNull(colVersion) ? -1 : cursor.getInt(colVersion));
        String ip = cursor.getString(colIP);
        int protocol = (cursor.isNull(colProtocol) ? -1 : cursor.getInt(colProtocol));
        int port = (cursor.isNull(colPort) ? -1 : cursor.getInt(colPort));
        String flags = cursor.getString(colFlags);
        final int uid = (cursor.isNull(colUid) ? -1 : cursor.getInt(colUid));
        int connection = (cursor.isNull(colConnection) ? -1 : cursor.getInt(colConnection));
        int interactive = (cursor.isNull(colInteractive) ? -1 : cursor.getInt(colInteractive));

        final String whois = (ip.length() > 1 && ip.charAt(0) == '/' ? ip.substring(1) : ip);

        // Get views
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        ImageView ivConnection = (ImageView) view.findViewById(R.id.ivConnection);
        ImageView ivInteractive = (ImageView) view.findViewById(R.id.ivInteractive);
        TextView tvProtocol = (TextView) view.findViewById(R.id.tvProtocol);
        TextView tvPort = (TextView) view.findViewById(R.id.tvPort);
        TextView tvFlags = (TextView) view.findViewById(R.id.tvFlags);
        ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        TextView tvUid = (TextView) view.findViewById(R.id.tvUid);
        TextView tvIP = (TextView) view.findViewById(R.id.tvIP);

        // Set values
        tvTime.setText(new SimpleDateFormat("HH:mm:ss").format(time));

        if (connection <= 0)
            ivConnection.setImageDrawable(null);
        else
            ivConnection.setImageResource(connection == 1 ? R.drawable.wifi_off : R.drawable.other_off);

        if (interactive <= 0)
            ivInteractive.setImageDrawable(null);
        else
            ivInteractive.setImageResource(R.drawable.screen_on);

        if (protocol == 1) // ICMP
            tvProtocol.setText("I");
        else if (protocol == 6) // TCP
            tvProtocol.setText("T");
        else if (protocol == 17) // UDP
            tvProtocol.setText("U");
        else
            tvProtocol.setText(protocol < 0 ? "" : Integer.toString(protocol));

        tvPort.setText(port < 0 ? "" : Integer.toString(port));
        tvFlags.setText(flags);

        // Application icon
        ApplicationInfo info = null;
        PackageManager pm = context.getPackageManager();
        String[] pkg = pm.getPackagesForUid(uid);
        if (pkg != null && pkg.length > 0)
            try {
                info = pm.getApplicationInfo(pkg[0], 0);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        if (info == null || info.icon == 0)
            ivIcon.setImageDrawable(null);
        else {
            Uri uri = Uri.parse("android.resource://" + info.packageName + "/" + info.icon);
            Picasso.with(context).load(uri).into(ivIcon);
        }

        tvUid.setText(uid < 0 ? "" : uid == 0 ? "root" : Integer.toString(uid % 100000));

        tvIP.setText(whois);
    }
}
