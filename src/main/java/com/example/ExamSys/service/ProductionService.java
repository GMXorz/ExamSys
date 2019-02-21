package com.example.ExamSys.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ExamSys.config.Constants;
import com.example.ExamSys.dao.ProductionRepository;
import com.example.ExamSys.dao.StudentRepository;
import com.example.ExamSys.domain.Production;
import com.example.ExamSys.domain.User;

@Service
public class ProductionService {
	private final Logger logger = LoggerFactory.getLogger(ProductionService.class);
	
	private static String studentProductionParentPath = Constants.STUDENT_PRODUCTION_PATH;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private ProductionRepository productionRepository;
	
	public boolean upLoadStudentProduction(Long stuid, String name, File file) {
		String oldProductionUrl = null;
		Long userId = studentRepository.findUserIdById(stuid);
		
		if((oldProductionUrl = productionRepository.findOneByUserIdAndName(userId, name)) != null) {
			File oldFile = new File(oldProductionUrl);
			oldFile.delete();
			logger.info("user: {} delete the old production successfully Name: {}", userId, oldFile.getName());
			productionRepository.deleteByUserIdAndName(userId, name);
		}
		
		String productionName = createProductionName(stuid, file);
		String url = saveProductionInLocalFS(studentProductionParentPath, file, productionName);
		if(url == null) {
			logger.error("user:{} Failed to save the production Name:{}", userId, file.getName());
			return false;
		}
		
		User user = studentRepository.findUserById(stuid);
		Production production = new Production();
		production.setName(name);
		production.setProductionUrl(url);
		production.setUser(user);
		productionRepository.save(production);
		logger.info("user: {} save the production successfully Name: {}", userId, file.getName());
		return true;
	}
	
	/*
	 * 将 production 保存到本地磁盘上，并返回存储路径
	 */
	private String saveProductionInLocalFS(String parentProductionPath, File file, String productionName) {
		
		String productionPath = null;
		try {
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] temp = new byte[inputStream.available()];
			File parentDir = new File(parentProductionPath);
			if(!parentDir.exists())
				parentDir.mkdirs();
			
			productionPath = parentDir.getPath() + File.separator + productionName;
			File out = new File(productionPath);
			if(!out.exists())
				out.createNewFile();
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(out));
			
			int len = 0;
			while((len = inputStream.read(temp)) != -1) {
				outputStream.write(temp, 0, len);
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
		} catch(FileNotFoundException exception) {
			logger.error("save production error: The file not found");
			exception.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return productionPath;
	}
	/*
	 * 创建作品文件名称，根据用户id和文件名
	 */
	private String createProductionName(Long stuid, File file) {
		StringBuilder productionName = new StringBuilder();
		
		Long userId = studentRepository.findUserIdById(stuid);
		if(userId != null)
			productionName.append(userId);
		else productionName.append("unknownuser");
		productionName.append("_");
		
		productionName.append(file.getName());
		return productionName.toString();
	}
}
