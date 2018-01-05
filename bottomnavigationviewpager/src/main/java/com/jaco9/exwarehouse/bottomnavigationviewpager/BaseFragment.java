package com.jaco9.exwarehouse.bottomnavigationviewpager;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaco9.exwarehouse.bottomnavigationviewpager.utils.SqlServerUtils;

import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by bruce on 2016/11/1.
 * BaseFragment
 */

public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    public static final String KEY_INFO="info";
    public final BaseFragment _this=this;
//    public static BaseFragment newInstance(String info) {
//        Bundle args = new Bundle();
//        BaseFragment fragment = new BaseFragment();
//        args.putString("info", info);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static BaseFragment newInstance(Integer info) {
        Bundle args = new Bundle();
        BaseFragment fragment = new BaseFragment();
        args.putInt(KEY_INFO,info);
        fragment.setArguments(args);
        return fragment;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_base, null);
//        TextView tvInfo = (TextView) view.findViewById(R.id.textView);
//        tvInfo.setText(getArguments().getString("info"));
//        tvInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "Don't click me.please!.", Snackbar.LENGTH_SHORT).show();
//            }
//        });
//        return view;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Integer fragmentIndex=getArguments().getInt(KEY_INFO);
        View view =null;
        switch (fragmentIndex) {
            case 0:
                view = inflater.inflate(R.layout.fragment_index, null);
                break;
            case 1:
                view = inflater.inflate(R.layout.fragment_outwarehouse, null);
//                ZXingView zXingView = (ZXingView) view.findViewById(R.id.zxingview);
//                IntentIntegrator.forSupportFragment(this).initiateScan();
                Button confirmButton = (Button) view.findViewById(R.id.confirm);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        输入出库数量
//                                点击按钮
//                        重新查询数据库并检查数量是否合法
                        EditText editTextItemExNum= (EditText)getView().findViewById(R.id.itemExNum);
                        TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.storeNum);
//                        TextView tvItemName = (TextView) getView().findViewById(R.id.itemName);
                        TextView tvItemId = (TextView) getView().findViewById(R.id.itemId);
                        TextView tvItemBarcode = (TextView) getView().findViewById(R.id.itemBarcode);
                        String strItemExNum=editTextItemExNum.getText().toString();
                        String strItemStoreNum=tvItemStoreNum.getText().toString();
                        String itemBarcode=tvItemBarcode.getText().toString();
                        if (StringUtils.isBlank(itemBarcode) || itemBarcode.length() <= 3) {
                            Toast.makeText(_this.getActivity(), "条形码，billno，id有误,请重新扫描或者检查数据!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        int length = itemBarcode.length();
                        String itemBillNo = itemBarcode.substring(0, length - 2);
                        String itemId = itemBarcode.substring(length - 2, length);
//                        String itemBillNo=tvItemBarcode.getText().toString();
//                        String itemId=tvItemId.getText().toString();
                        Float exNum=Float.parseFloat(strItemExNum);
                        Float storeNum=Float.parseFloat(strItemStoreNum);
                        if(exNum.floatValue()>storeNum.floatValue())
                        {
                            Toast.makeText(_this.getActivity(), "出库数量大于库存数量,请重新输入!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            UpdateLeftQuartyRunner updateLeftQuartyRunner=new UpdateLeftQuartyRunner();
//                            String strBillno=strings[0];
//                            String strId=strings[1];
//                            String strStoreNum=strings[2];
//                            String strOutNum=strings[3];
                            updateLeftQuartyRunner.execute(itemBillNo,itemId,strItemStoreNum,strItemExNum);
//                                开始事务
//                        增加出库条数
//                                减少库存
//                        提交事务
                        }

                    }
                });
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_help, null);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_index, null);

        }
//        View view = inflater.inflate(R.layout.fragment_base, null);
//        TextView tvInfo = (TextView) view.findViewById(R.id.textView);
//        tvInfo.setText(getArguments().getString("info"));
//        tvInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "Don't click me.please!.", Snackbar.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }




//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if(result != null) {
//            if(result.getContents() == null) {
//                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    @Override
    public void onResume() {
        Integer fragmentIndex=getArguments().getInt(KEY_INFO);
        if (fragmentIndex==1)
        {
            String scanResult=((HomeActivity)getActivity()).getBarCodeContent();
            Log.e(TAG, "fragment onResume! fragmentIndex=1 result="+scanResult);
            if (StringUtils.isNotBlank(scanResult)) {
                TextView tvItemBarcode = (TextView) getView().findViewById(R.id.itemBarcode);
                tvItemBarcode.setText(scanResult);
                QueryScanRunner mTask = new QueryScanRunner();
                mTask.execute(scanResult);
            }
        }
        super.onResume();
    }

    class QueryScanRunner extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            Log.i(TAG, "QueryScanRunner doInBackground(Params... params) called");
            if (strings != null && strings.length == 1) {
                String scanResult=strings[0];
//            根据扫码结果去数据库查询 校验结果是否合法(有且只有一条)
                if (StringUtils.isNotBlank(scanResult) && scanResult.length() >= 3) {
                    int length = scanResult.length();
                    String billNo = scanResult.substring(0, length - 2);
                    String id = scanResult.substring(length - 2, length);
                    Log.d(TAG, billNo + ":" + id);
                    String sql = " SELECT " +
                            " t.ItemName, " +
                            " t.BatchNo, " +
                            "  t.id, " +
                            " t.ReQty " +
                            " FROM " +
                            " WarehouseIn t " +
                            " WHERE " +
                            " t.BillNo = '" + billNo + "' " +
                            " AND t.ID = " + id + " ";
                    //                根据查询结果显示
                    String itemName = null;
                    String itemNo = null;
                    String itemId = null;
                    String itemBarcode = scanResult;
                    String itemStoreNum = null;
//                String connStr="jdbc:sqlserver://127.0.0.1:50788;databaseName=warehouse";
//                String userName="sa";
//                String userPsw="sa";
                    SqlServerUtils.init();
//                String sql="select t.BatchNo from WarehouseIn t";
                    ResultSet rs = SqlServerUtils.executeQuery(sql);
                    int count = 0;
                    if (rs == null) {
                        Log.e(TAG,sql+" rs is null!");
                        try {
                            SqlServerUtils.releaseConn();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    try {
                        rs.last();//移到最后一行
                        count = rs.getRow();
                        rs.beforeFirst();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (count != 1) {
                        Log.e(TAG, "rs count error!");
                        rs = null;
                        try {
                            SqlServerUtils.releaseConn();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    try {
                        while (rs.next()) {
//                        System.out.println(rs.getString(1));
                            itemName = rs.getString(1);
                            itemNo = rs.getString(2);
                            itemId = rs.getString(3);
                            itemStoreNum = rs.getString(4);
////                        View view=getActivity()
//                            TextView tvItemName = (TextView) getView().findViewById(R.id.itemName);
//                            tvItemName.setText(itemName);
//                            TextView tvItemNo = (TextView) getView().findViewById(R.id.itemNo);
//                            tvItemNo.setText(itemNo);
//                            TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.storeNum);
//                            tvItemStoreNum.setText(itemStoreNum);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    rs = null;
                    try {
                        SqlServerUtils.releaseConn();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return itemName+":"+itemNo+":"+itemId+":"+scanResult+":"+itemStoreNum;
                }
            }
            return null;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "QueryScanRunner onPostExecute(Result result) called");
            if (StringUtils.isNotBlank(result)&&result.split(":").length==5)
            {
                String[] resultArray=result.split(":");
                if (StringUtils.isNotBlank(resultArray[0])) {
                    TextView tvItemName = (TextView) getView().findViewById(R.id.itemName);
                    tvItemName.setText(resultArray[0]);
                }
                if (StringUtils.isNotBlank(resultArray[1])) {
                    TextView tvItemNo = (TextView) getView().findViewById(R.id.itemNo);
                    tvItemNo.setText(resultArray[1]);
                }
                if (StringUtils.isNotBlank(resultArray[2])) {
                    TextView tvItemId = (TextView) getView().findViewById(R.id.itemId);
                    tvItemId.setText(resultArray[2]);
                }
//                TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.storeNum);
//                tvItemStoreNum.setText(resultArray[3]);
                if (StringUtils.isNotBlank(resultArray[4])) {
                    TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.storeNum);
                    tvItemStoreNum.setText(resultArray[4]);
                }
            }
            else
            {
                Toast.makeText(_this.getActivity(), "数据库结果集错误,请检查!", Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateLeftQuartyRunner extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            Log.i(TAG, "UpdateLeftQuartyRunner doInBackground(Params... params) called");
            if (strings != null && strings.length == 4) {
                String strBillno=strings[0];
                String strId=strings[1];
                String strStoreNum=strings[2];
                String strOutNum=strings[3];

                if (StringUtils.isNotBlank(strBillno) &&StringUtils.isNotBlank(strId)&&StringUtils.isNotBlank(strOutNum) ) {
                    Float iStoreNum=Float.parseFloat(strStoreNum);
                    Float iOutNum=Float.parseFloat(strOutNum);
                    Float iLeftNum=iStoreNum-iOutNum;
                    Log.d(TAG, strBillno + ":" + strId+":"+iStoreNum+":"+iOutNum+":"+iLeftNum);
                    String insertSql = " insert into WarehouseOut(billno,id,outqty,buser,wdate)VALUES('"+strBillno+"',"+strId+","+strOutNum+",'手机',GETDATE())";
                    String updateSql="update WarehouseIn  set ReQty=ReQty-"+strOutNum+" where BillNo= '"+strBillno+"' and id="+strId+"";

                    SqlServerUtils.init();
                    SqlServerUtils.startTransaction();
                    int insResult=SqlServerUtils.executeUpdate(insertSql);
                    int updResult=SqlServerUtils.executeUpdate(updateSql);
                    if (insResult==1&&updResult==1) {
                        SqlServerUtils.endTransaction(true);
                        return "200";
                    }
                    else
                    {
                        SqlServerUtils.endTransaction(false);
                        return "300";
                    }

                }
                else
                {
                    return "400";
                }
            }
            return null;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute(Result result) called result:"+result);
            if (StringUtils.isNotBlank(result)&&result.equals("200"))
            {
                Toast.makeText(_this.getActivity(), "出库成功!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(_this.getActivity(), "出库失败,请检查!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
