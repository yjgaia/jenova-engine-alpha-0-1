package co.hanul.jenova.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import co.hanul.jenova.ErrorMsgContainer;
import co.hanul.jenova.domain.MapInfo;
import co.hanul.jenova.domain.MapTile;
import co.hanul.jenova.domain.Tile;

/**
 * 맵 서비스
 * 
 * @author Mr. 하늘
 */
@Service
public class MapService extends ServiceSupport {

	/**
	 * 맵 생성
	 */
	public MapInfo create(String name, Integer width, Integer height, ErrorMsgContainer errorMsgContainer) {
		MapInfo mapInfo = new MapInfo();
		
		mapInfo.setName(name);
		mapInfo.setWidth(width);
		mapInfo.setHeight(height);
		
		validate(mapInfo, errorMsgContainer);
		if (!errorMsgContainer.hasErrorMsgs()) {
			mapInfo.persist();
		}
		return mapInfo;
	}
	
	/**
	 * 타일 추가
	 */
	public MapTile addTile(Long id, Long tileId, Integer x, Integer y, ErrorMsgContainer errorMsgContainer) {
		MapInfo mapInfo = MapInfo.findMapInfo(id);
		Tile tile = Tile.findTile(tileId);
		if (x < 0 || x + tile.getWidth() > mapInfo.getWidth()) {
			errorMsgContainer.addErrorMsg("x가 범위를 벗어났습니다.");
		} else if (y < 0 || y + tile.getHeight() > mapInfo.getHeight()) {
			errorMsgContainer.addErrorMsg("y가 범위를 벗어났습니다.");
		} else {
			Set<MapTile> mapTiles = mapInfo.getMapTiles();
			boolean ok = true;
			for (MapTile mapTile : mapTiles) {
				int a1x = x, a1y = y;
				int a2x = x + tile.getWidth() - 1, a2y = y;
				int a3x = x + tile.getWidth() - 1, a3y = y + tile.getHeight() - 1;
				int a4x = x, a4y = y + tile.getHeight() - 1;
				
				int b1x = mapTile.getX(), b1y = mapTile.getY();
				int b2x = mapTile.getX() + mapTile.getTile().getWidth() - 1;
				int b4y = mapTile.getY() + mapTile.getTile().getHeight() - 1;
				
				if (
						((b1x <= a1x && a1x <= b2x) && (b1y <= a1y && a1y <= b4y)) ||
						((b1x <= a2x && a2x <= b2x) && (b1y <= a2y && a2y <= b4y)) ||
						((b1x <= a3x && a3x <= b2x) && (b1y <= a3y && a3y <= b4y)) ||
						((b1x <= a4x && a4x <= b2x) && (b1y <= a4y && a4y <= b4y))
					) {
					errorMsgContainer.addErrorMsg("해당 범위에 이미 타일이 존재합니다.");
					ok = false;
					break;
				}
			}
			if (ok) {
				MapTile mapTile = new MapTile();
				mapTile.setX(x);
				mapTile.setY(y);
				mapTile.setMapInfo(mapInfo);
				mapTile.setTile(tile);
				
				mapInfo.getMapTiles().add(mapTile);
				mapInfo.merge();
				return mapTile;
			}
		}
		return null;
	}

}
