package famicom.api.rom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RomInformation {
	/**
	 * ソフトの名前
	 * @return
	 */
	String name();
	/**
	 * 画面のミラーモード
	 * @return
	 */
	MirrorMode mirror() default MirrorMode.VERTICAL;
	/**
	 * バッテリーバックアップ(0x6000-0x7fff)を使うかどうか
	 * @return
	 */
	boolean backup() default false;
}
