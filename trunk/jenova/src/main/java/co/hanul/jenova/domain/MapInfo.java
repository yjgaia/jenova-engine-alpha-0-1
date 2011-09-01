package co.hanul.jenova.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class MapInfo {

	@NotNull(message = "넓이를 입력해 주십시오.")
	@DecimalMin(value = "1", message = "1 이상의 값을 입력해 주세요.")
	private Integer width;

	@NotNull(message = "높이를 입력해 주십시오.")
	@DecimalMin(value = "1", message = "1 이상의 값을 입력해 주세요.")
	private Integer height;

	@NotNull(message = "이름를 입력해 주십시오.")
	private String name;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<MapTile> mapTiles = new HashSet<MapTile>();
}
