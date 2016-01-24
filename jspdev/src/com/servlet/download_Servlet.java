package com.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.S3conn.S3download;

public class download_Servlet extends HttpServlet {
	private boolean checkfile(String fname){
    	File localfiles=new File("C:/Users/Xuefan/Desktop/640 HW4/jspdev2/jspdev/WebRoot/Files");
    	String filelist[];
    	filelist=localfiles.list();
    	for(String a:filelist){
    		if(a.equals(fname))
    			return true;
    	}
    	return false;
	}
	public void doGet(HttpServletRequest request,HttpServletResponse response)
			throws ServletException, IOException
			{
		   doPost(request,response);
			}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
			{
		request.setCharacterEncoding("gbk");
		response.setContentType("text/html;charset=gbk");
		//��ȡҪ���ص��ļ���
		int dstloca=Integer.parseInt(request.getParameter("req"));
        String filename = request.getParameter("name");
        boolean islocal=checkfile(filename);
        if(islocal){
    		//String filename="vcb_360p.mp4";
            //�õ���ͷ�������������
            OutputStream outputStream = response.getOutputStream();
            //����ļ��õ��ֽ����飬ÿ�������������600���ֽ�
            byte b[] = new byte[600];
            //Ҫ���ص��ļ�
            String ipaddr=request.getLocalAddr();
            File fileload = new File("C:/Users/Xuefan/Desktop/640 HW4/jspdev2/jspdev/WebRoot/Files",filename);   
            //File fileload=new File("http://"+ipaddr+":8080/jspDev/Files",filename);
            //�ͷ���ʹ�ñ����ļ��ĶԻ���
            response.setHeader("Content-disposition", "attachment;filename="+filename);
            //֪ͨ�ͷ��ļ���MIME����
            response.setContentType("application/msword");
            //֪ͨ�ͷ��ļ��ĳ���
            long fileLength = fileload.length();
            String length = String.valueOf(fileLength);
            response.setHeader("Content_length", length);
            //��ȡ�ļ��������͸��ͷ�������
            FileInputStream inputStream = new FileInputStream(fileload);
            int n = 0;
            while((n=inputStream.read(b))!=-1){
                outputStream.write(b,0,n);
            }
        }
        else{
        	BufferedOutputStream stream=null;
        	File file=null;
    		S3download sd=new S3download();
    		byte[] r=sd.download(filename, dstloca);
        	if(dstloca==0){
        		try{
        			file=new File("C:/Users/Xuefan/Desktop/640 HW4/jspdev2/jspdev/WebRoot/Files/"+filename);
        			FileOutputStream fstream=new FileOutputStream(file);
        			stream=new BufferedOutputStream(fstream);
        			stream.write(r);
        		}
        		catch (Exception e){
        			e.printStackTrace();
        		}
        		finally{
        			if(stream!=null){
        				try{
        					stream.close();
        				}
        				catch (IOException e1){
        					e1.printStackTrace();
        				}
        			}
        		}
                OutputStream outputStream = response.getOutputStream();
                byte b[] = new byte[600];
                String ipaddr=request.getLocalAddr();
                File fileload = new File("C:/Users/Xuefan/Desktop/640 HW4/jspdev2/jspdev/WebRoot/Files",filename);   
                response.setHeader("Content-disposition", "attachment;filename="+filename);
                response.setContentType("application/msword");
                long fileLength = fileload.length();
                String length = String.valueOf(fileLength);
                response.setHeader("Content_length", length);
                FileInputStream inputStream = new FileInputStream(fileload);
                int n = 0;
                while((n=inputStream.read(b))!=-1){
                    outputStream.write(b,0,n);
                }
        	}
        	else{
        		response.setHeader("Content-disposition", "attachment;filename="+filename);
                response.setContentType("application/msword");
                response.setHeader("Content_length", String.valueOf(r.length));
        		OutputStream outputStream = response.getOutputStream();
        		outputStream.write(r,0,r.length);
        	}
        }

			}
	}
