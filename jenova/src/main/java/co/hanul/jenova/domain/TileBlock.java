package co.hanul.jenova.domain;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class TileBlock {

	@NotNull(message = "x 좌표를 입력해 주십시오.")
    private Integer x;

	@NotNull(message = "y 좌표를 입력해 주십시오.")
    private Integer y;
}
