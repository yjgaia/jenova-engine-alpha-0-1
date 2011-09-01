package co.hanul.jenova.domain;

import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class MapTile {

	@ManyToOne
	@NotNull(message = "맵을 선택해 주십시오.")
	private MapInfo mapInfo;

	@NotNull(message = "x 좌표를 입력해 주십시오.")
	@DecimalMin(value = "0", message = "0 이상의 값을 입력해 주세요.")
	private Integer x;

	@NotNull(message = "y 좌표를 입력해 주십시오.")
	@DecimalMin(value = "0", message = "0 이상의 값을 입력해 주세요.")
	private Integer y;

	@ManyToOne
	@NotNull(message = "타일을 선택해 주십시오.")
	private Tile tile;
}
