package co.hanul.jenova.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import co.hanul.jenova.ErrorMsgContainer;
import co.hanul.jenova.ImageSize;
import co.hanul.jenova.JenovaConfig;
import co.hanul.jenova.domain.Tile;
import co.hanul.jenova.domain.TileBlock;

/**
 * 타일 서비스
 * 
 * @author Mr. 하늘
 */
@Service
public class TileService extends ServiceSupport {

	/**
	 * 타일 생성
	 */
	public Tile create(String name, String imageFilePath, String tileImageDirPath, ErrorMsgContainer errorMsgContainer) {
		Tile tile = new Tile();
		
		ImageSize imageSize = getImageSize(imageFilePath);
		
		File imageFile = new File(imageFilePath);
		if (!imageFile.isFile()) { // 파일이 아닐 경우
			errorMsgContainer.addErrorMsg("이미지 파일을 업로드 해 주시기 바랍니다.");
		} else if (!imageSizeValidate(imageSize)) { // 크기가 잘못되었을 경우
			errorMsgContainer.addErrorMsg("이미지 크기가 잘못되었습니다. 가로 세로가 " + JenovaConfig.BASIC_TILE_PIXEL + "px의 배수여야 합니다.");
		} else {
			tile.setWidth(imageSize.getWidth() / JenovaConfig.BASIC_TILE_PIXEL);
			tile.setHeight(imageSize.getHeight() / JenovaConfig.BASIC_TILE_PIXEL);
			tile.setName(name);
		}
		
		validate(tile, errorMsgContainer);
		if (!errorMsgContainer.hasErrorMsgs()) {
			tile.persist();
			try {
				FileUtils.copyFile(imageFile, new File(tileImageDirPath + File.separator + tile.getId()));
				FileUtils.forceDelete(imageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tile;
	}
	
	/**
	 * 블록 추가
	 */
	public Tile addBlock(Long id, Integer x, Integer y, ErrorMsgContainer errorMsgContainer) {
		Tile tile = Tile.findTile(id);
		if (x < 0 || x > tile.getWidth()) {
			errorMsgContainer.addErrorMsg("x가 범위를 벗어났습니다.");
		} else if (y < 0 || y > tile.getHeight()) {
			errorMsgContainer.addErrorMsg("y가 범위를 벗어났습니다.");
		} else {
			TileBlock tileBlock = new TileBlock();
			tileBlock.setX(x);
			tileBlock.setY(y);
			tile.getTileBlocks().add(tileBlock);
			tile.merge();
		}
		return tile;
	}

	/**
	 * 블록 제거
	 */
	public Tile removeBlock(Long id, Integer x, Integer y, ErrorMsgContainer errorMsgContainer) {
		Tile tile = Tile.findTile(id);
		if (x < 0 || x > tile.getWidth()) {
			errorMsgContainer.addErrorMsg("x가 범위를 벗어났습니다.");
		} else if (y < 0 || y > tile.getHeight()) {
			errorMsgContainer.addErrorMsg("y가 범위를 벗어났습니다.");
		} else {
			List<TileBlock> removeTileBlocks = new ArrayList<TileBlock>();
			for (TileBlock tileBlock : tile.getTileBlocks()) {
				if (tileBlock.getX() == x && tileBlock.getY() == y) {
					removeTileBlocks.add(tileBlock);
				}
			}
			for (TileBlock tileBlock : removeTileBlocks) {
				tile.getTileBlocks().remove(tileBlock); // 타일 블록 목록에서 제외
			}
			tile.merge();
			for (TileBlock tileBlock : removeTileBlocks) {
				tileBlock.remove(); // 삭제
			}
		}
		return tile;
	}

}
