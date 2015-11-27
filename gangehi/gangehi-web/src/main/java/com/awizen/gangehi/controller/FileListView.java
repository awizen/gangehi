package com.awizen.gangehi.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.awizen.gangehi.model.FileEntity;
import com.awizen.gangehi.service.PropertyService;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Named
@ViewScoped
public class FileListView implements Serializable {

	@Inject
	private Logger log;

	@Inject
	private PropertyService propertyService;

	private List<FileEntity> fileList = new ArrayList<FileEntity>();

	private FileEntity selected;

	private FileEntity file;

	private Long downloadFileId;

	private Long fakeFileId = -1l;

	public void onSelect(SelectEvent event) {
		selected = (FileEntity) event.getObject();
	}

	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		log.info("File uploaded: " + file.getFileName());
		FileEntity fe = new FileEntity();
		fe.setId(fakeFileId--);
		fe.setFileName(file.getFileName());
		fe.setContent(file.getContents());
		fe.setContentType(file.getContentType());

		fileList.add(fe);

	}

	public void deleteFile() {
		fileList.remove(selected);
	}

	public StreamedContent  getDownloadFile(){
		log.info("Download file id: " + downloadFileId);

		Map<Long, FileEntity> fileMap = toMapBy(fileList, FileEntity::getId);

		FileEntity fileEntity = fileMap.get(downloadFileId);


		InputStream stream = new ByteArrayInputStream(fileEntity.getContent());
		StreamedContent  streamedContent = new DefaultStreamedContent(stream, fileEntity.getContentType(), fileEntity.getFileName());

		return streamedContent;
	}

	public static <K, T> Map<K, T> toMapBy(List<T> list, Function<? super T, ? extends K> mapper) {
		return list.stream().collect(Collectors.toMap(mapper, Function.identity()));
	}

	public long getFileSizeLimit(){
		return propertyService.getLong("fileUpload.fileSizeLimit", 1000000l);
	}

	public String getFileSizeLimitMB(){
		double d = propertyService.getLong("fileUpload.fileSizeLimit", 1000000l);
		return new DecimalFormat("#.###").format(d / 1000000);
	}
}
