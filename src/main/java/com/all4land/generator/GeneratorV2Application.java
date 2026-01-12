package com.all4land.generator;

import javax.swing.SwingUtilities;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.all4land.generator.ui.controller.MainController;
import com.all4land.generator.ui.util.LookAndFeelUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class GeneratorV2Application {

	public static void main(String[] args) {
		// 인코딩 설정 (Windows 터미널 한글 깨짐 방지)
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("console.encoding", "UTF-8");
		
		//
		LookAndFeelUtils.setWindowsLookAndFeel();

        // Spring Application Context 생성
        ConfigurableApplicationContext context = createApplicationContext(args);

        // 메인 프레임 표시
        displayMainFrame(context);
	}

	private static ConfigurableApplicationContext createApplicationContext(String[] args) {
        return new SpringApplicationBuilder(GeneratorV2Application.class)
                .headless(false)
                .run(args);
    }

    private static void displayMainFrame(ConfigurableApplicationContext context) {
    	// EDT 실행(이벤트 디스패치 스레드)
        SwingUtilities.invokeLater(() -> {
            //
            MainController mainMenuController = context.getBean(MainController.class);
            try {
                mainMenuController.prepareAndOpenFrame();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }
}

