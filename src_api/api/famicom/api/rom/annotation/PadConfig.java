package famicom.api.rom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * キーボードのデフォルトを設定する
 * @author hkoba
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PadConfigHolder.class)
public @interface PadConfig {
	/**
	 * 0:プレイヤー１
	 * 1:プレイヤー2
	 * @return
	 */
	int number();

	/**
	 * KetEvent.VK_xxxを設定する
	 * @return
	 */
	int left();

	int right();
	
	int up();
	
	int down();

	int select();
	
	int start();
	
	int a();
	
	int b();
}
