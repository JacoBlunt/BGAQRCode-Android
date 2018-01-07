package com.jaco9.exwarehouse.bottomnavigationviewpager.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jaco on 2017/12/29.
 */

public class SqlServerUtils {
    private static Connection conn=null;
    private static final String TAG = "SqlServerUtils";
    private static Date endDate= DateUtil.str2Date("2018-04-30","yyyy-MM-dd");

    public static int init()
    {
//        String connStr="jdbc:sqlserver://192.168.123.200:1433;databaseName=warehouse";
        String connStr ="jdbc:jtds:sqlserver://192.168.123.200:1433/warehouse;charset=UTF-8;";
//        String connStr ="jdbc:jtds:sqlserver://172.16.0.13:1433/FabricWare;charset=UTF-8;";
        String userName="sa";
        String userPsw="changjianglu123";
        SqlServerUtils.dbConnect(connStr,userName,userPsw);
        String dateSql="select GETDATE() ";
        ResultSet rs=SqlServerUtils.executeQuery(dateSql);
        if (rs!=null)
        {
            try {
                rs.next();
                Date sqlDate=rs.getDate(1);
                if(sqlDate.before(endDate))
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
        {
            return -100;
        }
        return 0;
    }

//    dbConnect("jdbc:sqlserver://223.244.227.14:21006;databaseName=OnDemand", "xzsoft1", "xzsoft2");
    public static void dbConnect(String db_connect_string, String db_userid, String db_password) {
        if (conn==null) {
            try {
//                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
                System.out.println("connected");//如果执行到此,说明连接成功
//            return conn;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startTransaction()
    {
        try {
            if (conn!=null&&!conn.isClosed()) {

                    conn.setAutoCommit(false);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void endTransaction(boolean isCommit)
    {
        try {
            if (conn!=null&&!conn.isClosed()) {
                    if (isCommit)
                    {
                        conn.commit();
                    }
                    else
                    {
                        conn.rollback();
                    }
                    conn.setAutoCommit(true);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void releaseConn() throws SQLException {
        if (conn==null)
        {
            return;
        }
        else if (!conn.isClosed())
        {
                conn.close();
                conn=null;
        }
    }

    public static  ResultSet executeQuery(String sql)
    {
        if (conn==null)
        {
            Log.e(TAG,"conn is null");
            return null;
        }
        Statement statement = null;//用于执行查询语句
        ResultSet rs =null;
        try {
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        String queryString = "select * from sysobjects where type='u'";// 枚举所有表
        //唯有查询 用这个方法
//        while (rs.next()) {
//            System.out.println(rs.getString(1));
//        }
        return rs;
    }

    public static int executeUpdate(String sql)
    {
        if (conn==null)
            return -1;
        Statement statement = null;//用于执行查询语句
        int n =-1;
        try {
            statement = conn.createStatement();
            n = statement.executeUpdate(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return n;
    }


    public static void main(String[]args)
    {
        String connStr="jdbc:sqlserver://127.0.0.1:50788;databaseName=warehouse";
        String userName="sa";
        String userPsw="sa";
        SqlServerUtils.init();
//        SqlServerUtils.dbConnect(connStr,userName,userPsw);
        String sql="select t.BatchNo from WarehouseIn t";
        ResultSet rs=SqlServerUtils.executeQuery(sql);
        try {
            while (rs.next()) {
                System.out.println(rs.getString(1));
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
