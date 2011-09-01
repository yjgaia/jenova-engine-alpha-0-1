package co.hanul.jenova.service;

import java.awt.Image;

import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import co.hanul.jenova.ErrorMsgContainer;
import co.hanul.jenova.ImageSize;
import co.hanul.jenova.JenovaConfig;

/**
 * 서비스를 서포트
 * 
 * @author Mr. 하늘
 */
@Service
public abstract class ServiceSupport {

	@Autowired
	protected Validator validator;

	/**
	 * JSR 303 Validate
	 */
	protected void validate(Object obj, ErrorMsgContainer errorMsgContainer) {
		DataBinder binder = new DataBinder(obj);
		binder.setValidator(validator);
		binder.validate();
		BindingResult bindingResult = binder.getBindingResult();
		if (bindingResult.hasErrors()) {
			for (ObjectError error : bindingResult.getAllErrors()) {
				errorMsgContainer.addErrorMsg(error.getDefaultMessage());
			}
		}
	}
	
	/**
	 * 이미지 사이즈 구하기
	 */
	protected ImageSize getImageSize(String imageFilePath) {
		Image img = new ImageIcon(imageFilePath).getImage();
		ImageSize imageSize = new ImageSize();
		imageSize.setWidth(img.getWidth(null));
		imageSize.setHeight(img.getHeight(null));
		return imageSize;
	}
	
	/**
	 * 이미지 사이즈 유효성 검사
	 */
	protected boolean imageSizeValidate(ImageSize imageSize) {
		return imageSize.getWidth() > 0 &&
				imageSize.getHeight() > 0 &&
				imageSize.getWidth() % JenovaConfig.BASIC_TILE_PIXEL == 0 &&
				imageSize.getHeight() % JenovaConfig.BASIC_TILE_PIXEL == 0;
	}

}
