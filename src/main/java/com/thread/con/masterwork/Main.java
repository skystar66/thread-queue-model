package com.thread.con.masterwork;

import java.util.Calendar;
import java.util.List;

public class Main {

	public static void main(String[] args) {
//
//		List<Integer> list=new ArrayList<Integer>();
//		for(int i=1;i<52;i++){
//			list.add(i);
//		}
//		fenye(list,10);
//		System.out.println(Math.abs(5));

//		发送者 1188742971189764096
//		接收者 1188739124417404929

//
//		System.out.println(getMsgIndexTableName("1148826155868165246"));
		System.out.println(getMsgTableName("1148826155868165246"));

//
//		DButil.init();
//
//		Excutes.init();

//		Excutes.runTasky();
//		System.out.println(Excutes.runTasky());







		
	}



	public static long getMsgTableName(String msgId) {
		int numcode = msgId.hashCode();
		if (numcode == Integer.MIN_VALUE) {
			numcode = Integer.MAX_VALUE;
		}
		int num = Math.abs(numcode);
		long i = num % 512;
		// 获取表名
		return i;
	}




	/**
	 * 获取N天前的时间
	 */
	public static long getTimestapsByDays(int days) {
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DATE, -days);
		return calendar2.getTime().getTime();
	}

	public static long getMsgIndexTableName(String collectionKey) {
		int numcode = collectionKey.hashCode();
		if (numcode == Integer.MIN_VALUE) {
			numcode = Integer.MAX_VALUE;
		}
		int num = Math.abs(numcode);
		long i = num % 512;
		// 获取表名
		return i;
	}




	public static  void fenye(List list, int pagesize){
		int totalcount=list.size();
		int pagecount=0;
		int m=totalcount%pagesize;
		if  (m>0){
			pagecount=totalcount/pagesize+1;
		}else{
			pagecount=totalcount/pagesize;
		}
		for(int i=1;i<=pagecount;i++){
			if (m==0){
				List<Integer> subList= list.subList((i-1)*pagesize,pagesize*(i));
				System.out.println(subList);
			}else{
				if (i==pagecount){
					List<Integer> subList= list.subList((i-1)*pagesize,totalcount);
					System.out.println(subList);
				}else{
					List<Integer> subList= list.subList((i-1)*pagesize,pagesize*(i));
					System.out.println(subList);
				}
			}
		}
	}

}
