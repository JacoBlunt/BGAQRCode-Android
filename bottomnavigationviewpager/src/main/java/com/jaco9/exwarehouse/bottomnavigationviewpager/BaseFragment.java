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

import com.jaco9.exwarehouse.bottomnavigationviewpager.utils.DateUtil;
import com.jaco9.exwarehouse.bottomnavigationviewpager.utils.SqlServerUtils;

import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


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
                        TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.itemStoreNum);
//                        TextView tvItemName = (TextView) getView().findViewById(R.id.itemName);
                        //TextView tvItemId = (TextView) getView().findViewById(R.id.itemId);
                        TextView tvItemBarcode = (TextView) getView().findViewById(R.id.itemBarcode);
                        String strItemExNum=editTextItemExNum.getText().toString();
                        String strItemStoreNum=tvItemStoreNum.getText().toString();
                        String itemBarcode=tvItemBarcode.getText().toString();
                        if (StringUtils.isBlank(itemBarcode) || itemBarcode.length() <= 5) {
                            Toast.makeText(_this.getActivity(), "条形码，billno，id有误,请重新扫描或者检查数据!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        int length = itemBarcode.length();
                        //最后一位为校验位
                        String itemBillNo = itemBarcode.substring(0, length - 3);
                        String itemId = itemBarcode.substring(length - 3, length-1);
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
                EditText etItemExNum=(EditText)getView().findViewById(R.id.itemExNum);
                etItemExNum.setText("");
                TextView tvItemName = (TextView) getView().findViewById(R.id.itemBatchNo);
                tvItemName.setText("");
                TextView tvItemNo = (TextView) getView().findViewById(R.id.itemSupplyName);
                tvItemNo.setText("");
                TextView tvItemId = (TextView) getView().findViewById(R.id.itemInDate);
                tvItemId.setText("");
                TextView tvItemDesc = (TextView) getView().findViewById(R.id.itemDesc);
                tvItemDesc.setText("");
                TextView tvItemSpec = (TextView) getView().findViewById(R.id.itemSpec);
                tvItemSpec.setText("");
                TextView tvItemLocation = (TextView) getView().findViewById(R.id.itemLocation);
                tvItemLocation.setText("");
                TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.itemStoreNum);
                tvItemStoreNum.setText("");



                QueryScanRunner mTask = new QueryScanRunner();
                mTask.execute(scanResult);
            }
        }
        super.onResume();
    }

    class QueryScanRunner extends AsyncTask<String, Integer, WarehouseStoreVO>
    {

        @Override
        protected WarehouseStoreVO doInBackground(String... strings) {
            Log.i(TAG, "QueryScanRunner doInBackground(Params... params) called");
            WarehouseStoreVO warehouseStoreVO=new WarehouseStoreVO();
            if (strings != null && strings.length == 1) {
                String scanResult=strings[0];
//            根据扫码结果去数据库查询 校验结果是否合法(有且只有一条)
                if (StringUtils.isNotBlank(scanResult) && scanResult.length() >= 5) {
                    int length = scanResult.length();
                    String billNo = scanResult.substring(0, length - 3);
                    String id = scanResult.substring(length - 3, length-1);
                    Log.d(TAG, billNo + ":" + id);
                    String sql = "SELECT t.BatchNo, t.SupplyName, t.Wdate, t.Description, t.spec, t.Location, t.ReQty FROM WarehouseIn t " +
                            " WHERE  t.BillNo = '" + billNo + "' AND t.ID = " + id + " ";
                    //根据查询结果显示
                    String itemBatchNo = null;
                    String itemSupplyName = null;
                    Date itemInDate = null;
                    String itemDesc = null;
                    String itemSpec = null;
                    String itemLocation = null;
                    Float itemStoreNum = null;
                    String itemBarcode = scanResult;

//                String connStr="jdbc:sqlserver://127.0.0.1:50788;databaseName=warehouse";
//                String userName="sa";
//                String userPsw="sa";
                  int intResult=  SqlServerUtils.init();
                  if (intResult<=0)
                  {
                      Toast.makeText(_this.getActivity(), "600,获取对象出现问题!", Toast.LENGTH_LONG).show();
                      return null;
                  }
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
//                            String itemBatchNo = null;
//                            String itemSupplyName = null;
//                            Date itemInDate = null;
//                            String itemDesc = null;
//                            String itemSpec = null;
//                            String itemLocation = null;
//                            String itemStoreNum = null;

                            itemBatchNo = rs.getString(1);
                            itemSupplyName = rs.getString(2);
                            itemInDate = rs.getDate(3);
                            itemDesc = rs.getString(4);
                            itemSpec = rs.getString(5);
                            itemLocation = rs.getString(6);
                            itemStoreNum = rs.getFloat(7);

                            warehouseStoreVO.setItemBatchNo(itemBatchNo);
                            warehouseStoreVO.setItemSupplyName(itemSupplyName);
                            warehouseStoreVO.setItemInDate(itemInDate);
                            warehouseStoreVO.setItemDesc(itemDesc);
                            warehouseStoreVO.setItemSpec(itemSpec);
                            warehouseStoreVO.setItemLocation(itemLocation);
                            warehouseStoreVO.setItemStoreNum(itemStoreNum);
                            warehouseStoreVO.setItemBarcode(itemBarcode);
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
                    return warehouseStoreVO;
                }
            }
            return null;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(WarehouseStoreVO result) {
            Log.i(TAG, "QueryScanRunner onPostExecute(Result result) called");
            if (result!=null&&StringUtils.isNotBlank(result.getItemBarcode())&&StringUtils.isNotBlank(result.getItemBatchNo()))
            {
                String itemBatchNo = result.getItemBatchNo();
                String itemSupplyName = result.getItemSupplyName();
                Date dItemInDate = result.getItemInDate();
                String itemInDate= DateUtil.date2String(dItemInDate,"yyyy-MM-dd HH:mm:ss");
                String itemDesc = result.getItemDesc();
                String itemSpec = result.getItemSpec();
                String itemLocation = result.getItemLocation();
                Float itemStoreNum = result.getItemStoreNum();
                String itemBarcode=result.getItemBarcode();
                if (StringUtils.isNotBlank(itemBatchNo)) {
                    TextView tvItemBatchNo = (TextView) getView().findViewById(R.id.itemBatchNo);
                    tvItemBatchNo.setText(itemBatchNo);
                }
                if (StringUtils.isNotBlank(itemSupplyName)) {
                    TextView tvItemSupplyName = (TextView) getView().findViewById(R.id.itemSupplyName);
                    tvItemSupplyName.setText(itemSupplyName);
                }
                if (StringUtils.isNotBlank(itemInDate)) {
                    TextView tvItemInDate = (TextView) getView().findViewById(R.id.itemInDate);
                    tvItemInDate.setText(itemInDate);
                }
                if (StringUtils.isNotBlank(itemDesc)) {
                    TextView tvItemDesc = (TextView) getView().findViewById(R.id.itemDesc);
                    tvItemDesc.setText(itemDesc);
                }
                if (StringUtils.isNotBlank(itemSpec)) {
                    TextView tvItemSpec = (TextView) getView().findViewById(R.id.itemSpec);
                    tvItemSpec.setText(itemSpec);
                }
                if (StringUtils.isNotBlank(itemLocation)) {
                    TextView tvItemLocation = (TextView) getView().findViewById(R.id.itemLocation);
                    tvItemLocation.setText(itemLocation);
                }

                if (itemStoreNum!=null&&itemStoreNum.floatValue()>=0) {
                    TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.itemStoreNum);
                    tvItemStoreNum.setText(itemStoreNum.toString());
                }
                if (StringUtils.isNotBlank(itemBarcode)) {
                    TextView tvItemBarcode = (TextView) getView().findViewById(R.id.itemBarcode);
                    tvItemBarcode.setText(itemBarcode);
                }
            }
            else
            {
                Toast.makeText(_this.getActivity(), "数据库结果集为空,请检查!", Toast.LENGTH_LONG).show();
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

                    int intResult=  SqlServerUtils.init();
                    if (intResult<=0)
                    {
                        Toast.makeText(_this.getActivity(), "600,时间或者网络有问题!", Toast.LENGTH_LONG).show();
                        return "600";
                    }
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
            return "500";
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute(Result result) called result:"+result);
            if (StringUtils.isNotBlank(result)&&result.equals("200"))
            {
                Toast.makeText(_this.getActivity(), "出库成功!", Toast.LENGTH_LONG).show();
            }
            else if (StringUtils.isNotBlank(result)&&result.equals("600"))
            {
                Toast.makeText(_this.getActivity(), "请检查重试,错误状态:"+result, Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(_this.getActivity(), "出库失败,请检查!错误代码:"+result, Toast.LENGTH_LONG).show();
            }
        }
    }

    class WarehouseStoreVO
    {
        private String itemBatchNo = null;
        private String itemSupplyName = null;
        private Date itemInDate = null;
        private String itemDesc = null;
        private String itemSpec = null;
        private String itemLocation = null;
        private Float itemStoreNum = null;
        private String itemBarcode = null;

        public String getItemBatchNo() {
            return itemBatchNo;
        }

        public void setItemBatchNo(String itemBatchNo) {
            this.itemBatchNo = itemBatchNo;
        }

        public String getItemSupplyName() {
            return itemSupplyName;
        }

        public void setItemSupplyName(String itemSupplyName) {
            this.itemSupplyName = itemSupplyName;
        }

        public Date getItemInDate() {
            return itemInDate;
        }

        public void setItemInDate(Date itemInDate) {
            this.itemInDate = itemInDate;
        }

        public String getItemDesc() {
            return itemDesc;
        }

        public void setItemDesc(String itemDesc) {
            this.itemDesc = itemDesc;
        }

        public String getItemSpec() {
            return itemSpec;
        }

        public void setItemSpec(String itemSpec) {
            this.itemSpec = itemSpec;
        }

        public String getItemLocation() {
            return itemLocation;
        }

        public void setItemLocation(String itemLocation) {
            this.itemLocation = itemLocation;
        }

        public Float getItemStoreNum() {
            return itemStoreNum;
        }

        public void setItemStoreNum(Float itemStoreNum) {
            this.itemStoreNum = itemStoreNum;
        }

        public String getItemBarcode() {
            return itemBarcode;
        }

        public void setItemBarcode(String itemBarcode) {
            this.itemBarcode = itemBarcode;
        }
    }
}
