package com.jaco9.exwarehouse.bottomnavigationviewpager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
//                                开始事务
//                        增加出库条数
//                                减少库存
//                        提交事务
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

//            根据扫码结果去数据库查询 校验结果是否合法(有且只有一条)
            if (StringUtils.isNotBlank(scanResult)&&scanResult.length()>=3) {
                int length=scanResult.length();
                String billNo=scanResult.substring(0, length-2);
                String id=scanResult.substring(length-2, length);
                Log.d(TAG,billNo+":"+id);
                String sql="SELECT" +
                        "t.ItemName," +
                        "t.BatchNo," +
                        "t.ReQty" +
                        "FROM" +
                        "WarehouseIn t" +
                        "WHERE" +
                        "t.BillNo = '"+billNo+"'" +
                        "AND t.ID = "+id+"";
                //                根据查询结果显示
                String itemName=null;
                String itemNo=null;
                String itemBarcode=scanResult;
                String itemStoreNum=null;
//                String connStr="jdbc:sqlserver://127.0.0.1:50788;databaseName=warehouse";
//                String userName="sa";
//                String userPsw="sa";
                SqlServerUtils.init();
//                String sql="select t.BatchNo from WarehouseIn t";
                ResultSet rs=SqlServerUtils.executeQuery(sql);
                int count=0;
                try {
                    rs.last();//移到最后一行
                    count = rs.getRow();
                    rs.beforeFirst();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (count!=1)
                {
                    Log.e(TAG,"rs count error!");
                    return;
                }
                try {
                    while (rs.next()) {
//                        System.out.println(rs.getString(1));
                        itemName=rs.getString(1);
                        itemNo=rs.getString(2);
                        itemStoreNum=rs.getString(3);
//                        View view=getActivity()
                        TextView tvItemName = (TextView) getView().findViewById(R.id.itemName);
                        tvItemName.setText(itemName);
                        TextView tvItemNo = (TextView) getView().findViewById(R.id.itemNo);
                        tvItemNo.setText(itemNo);
                        TextView tvItemStoreNum = (TextView) getView().findViewById(R.id.storeNum);
                        tvItemStoreNum.setText(itemStoreNum);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rs=null;
                try {
                    SqlServerUtils.releaseConn();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


        }
        super.onResume();
    }
}
