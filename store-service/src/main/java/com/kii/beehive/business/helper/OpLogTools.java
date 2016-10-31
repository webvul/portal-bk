package com.kii.beehive.business.helper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class OpLogTools {

	private static Logger log = LoggerFactory.getLogger(OpLogTools.class);


	private String path;

	private String prefix;

	private String ext;

	private final String FORMAT = "yyyy-MM-dd";

	private AtomicReference<BufferedWriter>  writerRef=new AtomicReference<>();


	@Value("${beehive.portal.oplog.file:${user.home}/.beehive/data/log/beehive/op-log.csv}")
	private String fullPath;

	@PostConstruct
	public void init() {

		int idx = fullPath.lastIndexOf("/");

		path = fullPath.substring(0, idx);
        //如果目录不存在,创建
		File pathFile = new File(path);
		if( ! pathFile.exists()){
			pathFile.mkdirs();
		}

		String fileName = fullPath.substring(idx + 1, fullPath.length());

		int dotIdx = fileName.lastIndexOf(".");
		prefix = fileName.substring(0, dotIdx);
		ext = fileName.substring(dotIdx, fileName.length());

		doDailySwitch();

	}


	@Scheduled(cron = "0/3 * * * * ?")
	public void doFlush() {
		try {
			writerRef.get().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Scheduled(cron = "0 0 0 * * ?")
	public void doDailySwitch() {

		Date date = new Date();

		SimpleDateFormat format = new SimpleDateFormat(FORMAT);
		String datePrefix = format.format(date);

		String fileName = prefix + "." + datePrefix + ext;

		File file = new File(path, fileName);

		for(int i=0;i<3;i++) {
			try {
				file.createNewFile();
				BufferedWriter newWriter = new BufferedWriter(new FileWriter(file, true));

				BufferedWriter oldWriter=writerRef.getAndSet(newWriter);
				if(oldWriter!=null) {
					oldWriter.close();
				}
				break;
			} catch (IOException e) {
				log.error("oplog create new file fail,retry "+i, e);
			}
		}

	}


	@PreDestroy
	public void close() {


		try {
			writerRef.get().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Async
	public void write(List<String> list) {

		list.add(0,String.valueOf(System.currentTimeMillis()));

		String line = StringUtils.collectionToDelimitedString(list, ",");

		writeLine(line);
	}

	@Async
	public void write(String[] list) {

		String line = StringUtils.arrayToDelimitedString(list, ",");

		writeLine(line);
	}

	private void writeLine(String line) {
		try {
			writerRef.get().write(line);
			writerRef.get().newLine();
		} catch (IOException e) {
			log.error("oplog write fail", e);
		}
	}
}