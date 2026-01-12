package com.all4land.generator.ui.util;

import javax.swing.UIManager;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LookAndFeelUtils {
	//
	public static void setWindowsLookAndFeel() {
		//
		try {
			//
			UIManager.setLookAndFeel(new FlatMacDarkLaf());
			// UIManager.setLookAndFeel(new FlatDarculaLaf());
			// UIManager.setLookAndFeel(new FlatIntelliJLaf());
			// UIManager.setLookAndFeel(new FlatDarkLaf());
//			UIManager.setLookAndFeel(new FlatLightLaf());
			// UIManager.setLookAndFeel(new FlatMacLightLaf());
		} catch (Exception e) {
			//
			log.error(e.getMessage());
		}
	}
}

