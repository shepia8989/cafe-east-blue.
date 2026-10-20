package id.eastblue.cafe;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private final int navy = Color.rgb(8, 19, 38), blue = Color.rgb(0, 160, 220), gold = Color.rgb(255, 195, 70);
    private final ArrayList<Product> products = new ArrayList<>();
    private final ArrayList<CartItem> cart = new ArrayList<>();
    private LinearLayout cartBox;
    private TextView totalText, statusText;
    private BluetoothAdapter bluetoothAdapter;
    static class Product { String name; int price; Product(String n,int p){name=n;price=p;} }
    static class CartItem { Product product; int qty=1; CartItem(Product p){product=p;} }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(navy); getWindow().setNavigationBarColor(navy);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); seedProducts(); buildUi();
    }
    private void seedProducts() {
        products.add(new Product("Kopi East Blue",18000)); products.add(new Product("Americano",15000));
        products.add(new Product("Cafe Latte",22000)); products.add(new Product("Cappuccino",22000));
        products.add(new Product("Es Kopi Susu",20000)); products.add(new Product("Matcha Latte",23000));
        products.add(new Product("Cokelat",18000)); products.add(new Product("Lemon Tea",14000));
        products.add(new Product("French Fries",16000)); products.add(new Product("Croissant",17000));
    }
    private void buildUi() {
        ScrollView scroll = new ScrollView(this); scroll.setBackgroundColor(navy);
        LinearLayout page = new LinearLayout(this); page.setPadding(dp(16),dp(18),dp(16),dp(28)); page.setOrientation(LinearLayout.VERTICAL); scroll.addView(page);
        TextView logo = new TextView(this); logo.setText("☠  CAFE EAST BLUE  ☠"); logo.setTextColor(gold); logo.setTextSize(25); logo.setTypeface(Typeface.DEFAULT,Typeface.BOLD); logo.setGravity(Gravity.CENTER); page.addView(logo,matchWrap());
        TextView subtitle = new TextView(this); subtitle.setText("PIRATE CAFE • POINT OF SALE"); subtitle.setTextColor(Color.LTGRAY); subtitle.setGravity(Gravity.CENTER); subtitle.setTextSize(12); subtitle.setPadding(0,dp(5),0,dp(18)); page.addView(subtitle,matchWrap());
        page.addView(heading("MENU KAFE"),matchWrap());
        for(Product p:products){
            LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(10),dp(8),dp(8),dp(8)); row.setBackgroundColor(Color.rgb(17,36,62)); LinearLayout.LayoutParams rp=matchWrap(); rp.bottomMargin=dp(7); page.addView(row,rp);
            LinearLayout labels=new LinearLayout(this); labels.setOrientation(LinearLayout.VERTICAL);
            TextView name=new TextView(this); name.setText(p.name); name.setTextColor(Color.WHITE); name.setTextSize(15); name.setTypeface(null,Typeface.BOLD); labels.addView(name);
            TextView price=new TextView(this); price.setText(rupiah(p.price)); price.setTextColor(gold); price.setTextSize(13); labels.addView(price);
            row.addView(labels,new LinearLayout.LayoutParams(0,-2,1)); Button add=new Button(this); add.setText("+ TAMBAH"); add.setTextSize(11); add.setTextColor(Color.WHITE); add.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue)); add.setOnClickListener(v->addToCart(p)); row.addView(add);
        }
        page.addView(heading("PESANAN"),matchWrap()); cartBox=new LinearLayout(this); cartBox.setOrientation(LinearLayout.VERTICAL); cartBox.setPadding(dp(12),dp(10),dp(12),dp(10)); cartBox.setBackgroundColor(Color.rgb(17,36,62)); page.addView(cartBox,matchWrap());
        totalText=new TextView(this); totalText.setTextColor(gold); totalText.setTextSize(20); totalText.setTypeface(null,Typeface.BOLD); totalText.setGravity(Gravity.RIGHT); totalText.setPadding(0,dp(12),0,dp(12)); page.addView(totalText,matchWrap());
        Button print=new Button(this); print.setText("CETAK STRUK • THERMAL 58 MM"); print.setTextColor(Color.WHITE); print.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue)); print.setOnClickListener(v->choosePrinter()); page.addView(print,matchWrap());
        Button clear=new Button(this); clear.setText("KOSONGKAN PESANAN"); clear.setOnClickListener(v->{cart.clear();refreshCart();}); page.addView(clear,matchWrap());
        statusText=new TextView(this); statusText.setText("Printer belum dipilih. Pasangkan printer lewat Pengaturan Bluetooth."); statusText.setTextColor(Color.LTGRAY); statusText.setTextSize(12); statusText.setPadding(0,dp(10),0,0); page.addView(statusText,matchWrap()); setContentView(scroll); refreshCart();
    }
    private TextView heading(String s){TextView t=new TextView(this);t.setText(s);t.setTextColor(gold);t.setTextSize(17);t.setTypeface(null,Typeface.BOLD);t.setPadding(0,dp(14),0,dp(10));return t;}
    private void addToCart(Product p){for(CartItem i:cart)if(i.product==p){i.qty++;refreshCart();return;}cart.add(new CartItem(p));refreshCart();}
    private void refreshCart(){if(cartBox==null)return;cartBox.removeAllViews();if(cart.isEmpty()){TextView e=new TextView(this);e.setText("Belum ada pesanan.");e.setTextColor(Color.LTGRAY);cartBox.addView(e);}else for(CartItem i:cart){TextView t=new TextView(this);t.setText(i.product.name+"  x"+i.qty+"    "+rupiah(i.product.price*i.qty));t.setTextColor(Color.WHITE);t.setTextSize(14);t.setPadding(0,dp(5),0,dp(5));cartBox.addView(t);}totalText.setText("TOTAL  "+rupiah(total()));}
    private int total(){int n=0;for(CartItem i:cart)n+=i.product.price*i.qty;return n;}
    private String rupiah(int n){return NumberFormat.getCurrencyInstance(new Locale("id","ID")).format(n).replace(",00","");}
    private void choosePrinter(){
        if(cart.isEmpty()){toast("Tambahkan menu ke pesanan terlebih dahulu.");return;} if(bluetoothAdapter==null){toast("HP ini tidak mendukung Bluetooth.");return;}
        if(Build.VERSION.SDK_INT>=31&&checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT},21);return;}
        try{if(!bluetoothAdapter.isEnabled()){toast("Aktifkan Bluetooth di pengaturan HP.");return;}Set<BluetoothDevice> paired=bluetoothAdapter.getBondedDevices();ArrayList<BluetoothDevice> ds=new ArrayList<>();ArrayList<String> ns=new ArrayList<>();for(BluetoothDevice d:paired){ds.add(d);String n=d.getName();ns.add((n==null?"Perangkat Bluetooth":n)+"\n"+d.getAddress());}if(ds.isEmpty()){toast("Pasangkan printer dari Pengaturan Bluetooth terlebih dahulu.");return;}new AlertDialog.Builder(this).setTitle("Pilih Printer Bluetooth").setItems(ns.toArray(new String[0]),(dialog,which)->printReceipt(ds.get(which))).setNegativeButton("Batal",null).show();}catch(SecurityException e){toast("Izin Bluetooth belum diberikan.");}
    }
    private void printReceipt(BluetoothDevice device){statusText.setText("Menghubungkan ke printer...");new Thread(()->{BluetoothSocket socket=null;try{UUID spp=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");socket=device.createRfcommSocketToServiceRecord(spp);socket.connect();OutputStream out=socket.getOutputStream();out.write(buildReceipt().getBytes(Charset.forName("CP437")));out.write(new byte[]{10,10,10});out.flush();runOnUiThread(()->statusText.setText("Struk berhasil dikirim ke printer."));runOnUiThread(()->Toast.makeText(this,"Struk dikirim ke printer.",Toast.LENGTH_SHORT).show());}catch(Exception e){runOnUiThread(()->{statusText.setText("Gagal mencetak. Pastikan printer menyala dan sudah dipasangkan.");Toast.makeText(this,"Gagal mencetak: "+e.getMessage(),Toast.LENGTH_LONG).show();});}finally{if(socket!=null)try{socket.close();}catch(Exception ignored){}}}).start();}
    private String buildReceipt(){StringBuilder b=new StringBuilder();b.append((char)27).append('@');b.append((char)27).append('a').append((char)1);b.append("CAFE EAST BLUE\nPIRATE CAFE - EAST BLUE\n--------------------------------\n");b.append((char)27).append('a').append((char)0);b.append("STRUK PEMBELIAN\n--------------------------------\n");for(CartItem i:cart)b.append(i.product.name).append("\n").append(i.qty).append(" x ").append(i.product.price).append(" = ").append(i.product.price*i.qty).append("\n");b.append("--------------------------------\nTOTAL: ").append(total()).append("\n--------------------------------\nTerima kasih!\nSampai jumpa, Nakama!\n\n");return b.toString();}
    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results){super.onRequestPermissionsResult(requestCode,permissions,results);if(requestCode==21&&results.length>0&&results[0]==PackageManager.PERMISSION_GRANTED)choosePrinter();else if(requestCode==21)toast("Izin Bluetooth diperlukan untuk mencetak struk.");}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();} private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);} private LinearLayout.LayoutParams matchWrap(){return new LinearLayout.LayoutParams(-1,-2);}
}
