package com.all4land.generator.ui.component;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.all4land.generator.ui.editor.MmsiTableCheckBoxEditor;
import com.all4land.generator.ui.renderer.MmsiTableCheckboxRenderer;

import lombok.extern.slf4j.Slf4j;

/**
 * 테이블 컴포넌트를 Spring Bean으로 등록하는 Configuration 클래스
 * generator 프로젝트의 MmsiComponent와 동일한 방식으로 테이블을 생성합니다.
 */
@Slf4j
@Configuration
public class TableComponent {

	/**
	 * MMSI 테이블 Bean 생성
	 */
	@Bean(name = "mmsiJTableName")
	JTable mmsiJTable() {
		String[] columnNames = {"MMSI", "AIS", "ASM", "VDE"};
		Object[][] data = {};
		
		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// MMSI 컬럼(0)은 편집 불가, AIS(1), ASM(2), VDE(3)는 편집 가능
				return column >= 1 && column <= 3;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return String.class; // MMSI 컬럼
				} else {
					return Boolean.class; // AIS, ASM, VDE 컬럼
				}
			}
		};
		
		JTable jTable = new JTable(tableModel);
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jTable.setShowGrid(true);
		
		// 체크박스 렌더러 설정 (AIS, ASM, VDE 컬럼)
		MmsiTableCheckboxRenderer checkboxRenderer = new MmsiTableCheckboxRenderer();
		jTable.getColumnModel().getColumn(1).setCellRenderer(checkboxRenderer); // AIS
		jTable.getColumnModel().getColumn(2).setCellRenderer(checkboxRenderer); // ASM
		jTable.getColumnModel().getColumn(3).setCellRenderer(checkboxRenderer); // VDE
		
		// 체크박스 에디터 설정 (AIS, ASM, VDE 컬럼)
		MmsiTableCheckBoxEditor checkboxEditor = new MmsiTableCheckBoxEditor();
		jTable.getColumnModel().getColumn(1).setCellEditor(checkboxEditor); // AIS
		jTable.getColumnModel().getColumn(2).setCellEditor(checkboxEditor); // ASM
		jTable.getColumnModel().getColumn(3).setCellEditor(checkboxEditor); // VDE
		
		log.debug("MMSI 테이블 Bean 생성 완료");
		return jTable;
	}

	/**
	 * Upper Frame A 채널 테이블 Bean 생성
	 */
	@Bean(name = "currentFrameTableUpperA")
	JTable currentFrameTableUpperA() {
		JTable jTable = createSlotTable(true, true);
		log.debug("Upper Frame A 채널 테이블 Bean 생성 완료");
		return jTable;
	}

	/**
	 * Upper Frame B 채널 테이블 Bean 생성
	 */
	@Bean(name = "currentFrameTableUpperB")
	JTable currentFrameTableUpperB() {
		JTable jTable = createSlotTable(true, false);
		log.debug("Upper Frame B 채널 테이블 Bean 생성 완료");
		return jTable;
	}

	/**
	 * Lower Frame 테이블 Bean 생성
	 */
	@Bean(name = "currentFrameTableLower")
	JTable currentFrameTableLower() {
		JTable jTable = createSlotTable(false, false);
		log.debug("Lower Frame 테이블 Bean 생성 완료");
		return jTable;
	}

	/**
	 * 슬롯 정보를 표시하는 TDMA 프레임 테이블 생성
	 */
	private JTable createSlotTable(boolean isUpper, boolean isChannelA) {
		DefaultTableModel model = new DefaultTableModel();
		JTable table = new JTable(model);
		
		// 헤더 생성: TDMA 레이블 + 슬롯 번호들 (32개 컬럼)
		model.addColumn("TDMA");
		for (int i = 0; i < 15; i++) {
			model.addColumn("");
		}
		model.addColumn("TDMA");
		for (int i = 0; i < 15; i++) {
			model.addColumn("");
		}
		
		// 헤더 숨기기
		table.getTableHeader().setVisible(false);
		
		// TDMA 세트 13개 (0~12), 각 세트마다 6개 채널 (TDMA 0~5)
		int maxSets = 13;
		for (int aa = 0; aa < maxSets; aa++) {
			for (int a = 0; a < 6; a++) {
				Object[] rowData = new Object[32];
				
				// 첫 번째 TDMA 섹션 (col 0~15)
				rowData[0] = "TDMA" + a;
				for (int col = 1; col <= 15; col++) {
					int slotNumber = a + ((col - 1) * 6) + (aa * 180);
					if (slotNumber < 2250) {
						rowData[col] = slotNumber;
					} else {
						rowData[col] = "";
					}
				}
				
				// 두 번째 TDMA 섹션 (col 16~31)
				if (aa < 12) {
					rowData[16] = "TDMA" + a;
					for (int col = 17; col <= 31; col++) {
						int slotNumber = a + ((col - 2) * 6) + (aa * 180);
						if (slotNumber < 2250) {
							rowData[col] = slotNumber;
						} else {
							rowData[col] = "";
						}
					}
				} else {
					for (int col = 16; col < 32; col++) {
						rowData[col] = "";
					}
				}
				
				model.addRow(rowData);
			}
			
			// TDMA5 다음에 간격 행 추가 (마지막 세트 제외)
			if (aa < maxSets - 1) {
				Object[] separatorRow = new Object[32];
				for (int col = 0; col < 32; col++) {
					separatorRow[col] = "";
				}
				model.addRow(separatorRow);
			}
		}
		
		// 셀 렌더러 설정
		table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
			@Override
			public java.awt.Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				java.awt.Component c = super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);
				
				// TDMA 레이블 셀
				if (column == 0 || column == 16) {
					c.setBackground(new Color(60, 60, 60));
					c.setForeground(Color.WHITE);
				} else {
					Object cellValue = table.getValueAt(row, column);
					if (cellValue != null && !cellValue.toString().isEmpty()) {
						c.setBackground(new Color(40, 40, 40));
						c.setForeground(Color.LIGHT_GRAY);
					} else {
						int[] separatorRows = {6, 13, 20, 27, 34, 41, 48, 55, 62, 69, 76, 83};
						boolean isSeparatorRow = false;
						for (int sepRow : separatorRows) {
							if (row == sepRow) {
								isSeparatorRow = true;
								break;
							}
						}
						if (isSeparatorRow) {
							c.setBackground(new Color(20, 20, 20));
							c.setForeground(new Color(20, 20, 20));
						} else {
							c.setBackground(new Color(30, 30, 30));
							c.setForeground(Color.DARK_GRAY);
						}
					}
				}
				
				((javax.swing.JLabel) c).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				return c;
			}
		});
		
		// 행 높이 설정
		table.setRowHeight(22);
		int[] separatorRows = {6, 13, 20, 27, 34, 41, 48, 55, 62, 69, 76, 83};
		for (int rowNum : separatorRows) {
			if (rowNum < model.getRowCount()) {
				table.setRowHeight(rowNum, 10);
			}
		}
		
		// 그리드 라인 표시
		table.setShowGrid(true);
		table.setGridColor(new Color(80, 80, 80));
		
		// 컬럼 크기 조정
		TableColumnModel columnModel = table.getColumnModel();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		columnModel.getColumn(0).setPreferredWidth(60);
		columnModel.getColumn(0).setMinWidth(50);
		columnModel.getColumn(0).setMaxWidth(80);
		
		if (columnModel.getColumnCount() > 16) {
			columnModel.getColumn(16).setPreferredWidth(60);
			columnModel.getColumn(16).setMinWidth(50);
			columnModel.getColumn(16).setMaxWidth(80);
		}
		
		// 슬롯 번호 컬럼들
		int slotColumnWidth = Math.max(30, 30);
		for (int i = 1; i < 16; i++) {
			if (i < columnModel.getColumnCount()) {
				columnModel.getColumn(i).setPreferredWidth(slotColumnWidth);
				columnModel.getColumn(i).setMinWidth(37);
			}
		}
		for (int i = 17; i < 32; i++) {
			if (i < columnModel.getColumnCount()) {
				columnModel.getColumn(i).setPreferredWidth(slotColumnWidth);
				columnModel.getColumn(i).setMinWidth(37);
			}
		}
		
		table.setIntercellSpacing(new Dimension(2, 2));
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(false);
		
		return table;
	}
}

