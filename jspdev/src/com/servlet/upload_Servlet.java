package com.servlet;
import javax.servlet.*;  
import javax.servlet.http.*;  

import java.io.*;  
import java.util.*;  

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;  
import org.apache.commons.fileupload.disk.*;

import com.DAO.DAO;
import com.S3conn.S3upload;
public class upload_Servlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String filePath;    // �ļ����Ŀ¼  
    private String tempPath;    // ��ʱ�ļ�Ŀ¼  
 
    // ��ʼ��  
    public void init(ServletConfig config) throws ServletException  
    {  
        super.init(config);  
        // �������ļ��л�ó�ʼ������  
        //filePath = config.getInitParameter("filepath");  
        //tempPath = config.getInitParameter("temppath");  
        filePath="/Files";
        tempPath="/tmpFiles";
        ServletContext context = getServletContext();  
        System.out.println(filePath+"\n"+tempPath);
        filePath = context.getRealPath(filePath);  
        tempPath = context.getRealPath(tempPath);  
        //���·�������ڣ��򴴽�·��
        File pathFile = new File(filePath);
        File pathTemp = new File(tempPath);
        if(!pathFile.exists()){
        	pathFile.mkdirs();
        }
        if(!pathTemp.exists()){
        	pathTemp.mkdirs();
        }
        System.out.println("�ļ����Ŀ¼����ʱ�ļ�Ŀ¼׼����� ...");  
        System.out.println("���Ŀ¼:"+pathFile.getAbsolutePath());
        System.out.println("��ʱĿ¼:"+pathTemp.getAbsolutePath());
    }  
      
    // doPost  
    public void doPost(HttpServletRequest req, HttpServletResponse res)  
        throws IOException, ServletException  
    {  
        res.setContentType("text/plain;charset=gbk");  
        PrintWriter pw = res.getWriter();  
        try{  
            DiskFileItemFactory diskFactory = new DiskFileItemFactory();  
            // threshold ���ޡ��ٽ�ֵ����Ӳ�̻��� 1G 
            diskFactory.setSizeThreshold(1000 * 1024 * 1024);  
            // repository �����ң�����ʱ�ļ�Ŀ¼  
            diskFactory.setRepository(new File(tempPath));  
          
            ServletFileUpload upload = new ServletFileUpload(diskFactory);  
            // ���������ϴ�������ļ���С 1G 
            upload.setSizeMax(1000 * 1024 * 1024);  
            // ����HTTP������Ϣͷ  
            List<FileItem> fileItems = upload.parseRequest(new ServletRequestContext(req));  
            Iterator<FileItem> iter = fileItems.iterator();  
            int num=Integer.parseInt(req.getParameter("priv"));
            while(iter.hasNext())  
            {  
                FileItem item = (FileItem)iter.next();  
                if(item.isFormField())  
                {  
                    System.out.println("��������� ...");  
                    processFormField(item, pw);  
                }else{  
                    System.out.println("�����ϴ����ļ� ...");  
                    processUploadFile(item, pw,num);  
                }  
            }// end while()  
 
            pw.close();  
        }catch(Exception e){  
            System.out.println("ʹ�� fileupload ��ʱ�����쳣 ...");  
            e.printStackTrace();  
        }// end try ... catch ...  
    }// end doPost()  
 
 
    // ���������  
    private void processFormField(FileItem item, PrintWriter pw)  
        throws Exception  
    {  
        String name = item.getFieldName();  
        String value = item.getString();          
        pw.println(name + " : " + value + "\r\n");  
    }  
      
    // �����ϴ����ļ�  
    private void processUploadFile(FileItem item, PrintWriter pw,int mynum)  
        throws Exception  
    {  
        // ��ʱ���ļ���������������·������ע��ӹ�һ��
        String filename = item.getName();         
        System.out.println("�������ļ�����" + filename);  
        int index = filename.lastIndexOf("\\");  
        filename = filename.substring(index + 1, filename.length());  
 
        long fileSize = item.getSize();  
 
        if("".equals(filename) && fileSize == 0)  
        {             
            System.out.println("�ļ���Ϊ�� ...");  
            return;  
        }  
 
        File uploadFile = new File(filePath + "/" + filename);  
        if(!uploadFile.exists()){
        	uploadFile.createNewFile();
        }
        item.write(uploadFile);  
        S3upload s3bean=new S3upload();
        s3bean.upload(uploadFile, filename,mynum);
        if(mynum!=0){
        	uploadFile.delete();
        }
        else{
        DAO d=new DAO();
        d.addFiletodb(filename);
        }
        pw.println(filename + " File Saved ...");  
        pw.println("File Size:" + fileSize + "\r\n");  
    }  
      
    public void doGet(HttpServletRequest req, HttpServletResponse res)  
        throws IOException, ServletException  
    {  
        doPost(req, res);  
    }  
}
/*
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class upload_Servlet  extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
			{
		String tempFileName=new String("tempFileName");
		File tempFile=new File("D:/"+tempFileName);
		FileOutputStream outputStream=new FileOutputStream(tempFile);
		InputStream fileSource1=request.getInputStream();
		byte b[]=new byte[1000];
		int n;
		while((n=fileSource1.read(b))!=-1){
			outputStream.write(b,0,n);
		}
		outputStream.close();
		fileSource1.close();
		RandomAccessFile randomFile=new RandomAccessFile(tempFile,"r");
		randomFile.readLine();
		String filePath=randomFile.readLine();
		int position=filePath.lastIndexOf('\\');
		String filename=filePath.substring(position,filePath.length()-1);
		randomFile.seek(0);
		long forthEnterPosition=0;
		int forth=1;
		while((n=randomFile.readByte())!=-1&&(forth<=4)){
			if(n=='\n'){
				forthEnterPosition=randomFile.getFilePointer();
				forth++;
			}
		}
		File fileupLoad=new File("D/wkspc","upload");
		fileupLoad.mkdir();
		File saveFile=new File("D:/wkspc/upload",filename);
		RandomAccessFile randomAccessFile=new RandomAccessFile(saveFile,"rw");
		randomFile.seek(randomFile.length());
		long endPosition=randomFile.getFilePointer();
		int j=1;
		while((endPosition>=0)&&(j<=4)){
			endPosition--;
			randomFile.seek(endPosition);
			if(randomFile.readByte()=='\n'){
				j++;
			}
		}
		randomFile.seek(forthEnterPosition);
		long startPoint=randomFile.getFilePointer();
		while(startPoint<endPosition){
			randomAccessFile.write(randomFile.readByte());
			startPoint=randomFile.getFilePointer();
		}
		randomAccessFile.close();
		randomFile.close();
		tempFile.delete();
			}

}*/
