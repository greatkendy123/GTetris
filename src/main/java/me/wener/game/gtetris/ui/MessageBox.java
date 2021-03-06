package me.wener.game.gtetris.ui;

import lombok.Getter;
import me.wener.game.gtetris.ui.components.*;
import me.wener.game.gtetris.ui.components.GDialogPanel.DialogButton;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Timer;
import java.util.TimerTask;

public final class MessageBox
		implements DialogResultListener {
	static GLayeredPane dialogLayeredPane;
	static GPanel notifyPanel;
	static Timer timer;

	static {
		timer = new Timer();
		dialogLayeredPane = new GLayeredPane();
		notifyPanel = new GPanel();
		notifyPanel.setOpaque(false);
		dialogLayeredPane.setLayer(notifyPanel, GLayeredPane.POPUP_LAYER + 100);
		dialogLayeredPane.add(notifyPanel);

		dialogLayeredPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent arg0) {
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				notifyPanel.setSize(dialogLayeredPane.getSize());
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
			}
		});
	}


	@Getter
	DialogResultListener resultListener;
	@Getter
	GDialogPanel dialogPanel;

	private MessageBox(GDialogPanel dialogPanel) {
		this.dialogPanel = dialogPanel;
		resultListener = dialogPanel.getResultListener();
		dialogPanel.setResultListener(this);
	}


	public static GLayeredPane getDialogLayeredPane() {
		return dialogLayeredPane;
	}

	public static void Show(String title, String content, DialogResultListener listener) {
		GDialogPanel dialogPanel = new GDialogPanel();
		dialogPanel
				.setTitle(title)
				.setContent(content)
				.setResultListener(listener)
				.setButtons(DialogButton.OK)
		;

		ShowIt(dialogPanel);
	}

	public static MessageBox Build(String title, String content, DialogResultListener listener) {
		return Build(title, content, new DialogButton[]{DialogButton.OK}, listener);
	}

	public static MessageBox Build(String title, String content, DialogButton[] buttons, DialogResultListener listener) {
		GDialogPanel dialogPanel = new GDialogPanel();
//		dialogPanel.setSize(dialogLayeredPane.getSize());
		dialogPanel.setLocation(0, 0);
		dialogPanel
				.setTitle(title)
				.setContent(content)
				.setResultListener(listener)
				.setButtons(buttons)
		;
		dialogPanel.setSize(dialogLayeredPane.getSize());
		return new MessageBox(dialogPanel);
	}

	private static void ShowIt(JComponent componet) {
		dialogLayeredPane.setLayer(componet, dialogLayeredPane.highestLayer() + 1);
		dialogLayeredPane.add(componet);
	}

	/**
	 * 显示提示,默认为2s
	 */
	public static GLabel ShowNotify(String text) {
		return ShowNotify(text, 2000);
	}

	/**
	 * 显示提示
	 *
	 * @param ms 显示的时间长度,<= 0 为持续显示
	 */
	public static GLabel ShowNotify(String text, int ms) {
		final GNotifyLabel label = new GNotifyLabel(text);
		label.setSize(label.getPreferredSize());
		int y = notifyPanel.getComponents().length * (label.getHeight() + 5) + 5;
		label.setLocation(0, y);
		notifyPanel.add(label);

		if (ms > 0)
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					label.setVisible(false);
					notifyPanel.remove(label);
				}
			}, ms);
		notifyPanel.repaint();
		return label;
	}

	public static void clearAllNotify() {
		notifyPanel.removeAll();
	}

	public void Show() {
		ShowIt(dialogPanel);
	}

	@Override
	public void OnDialogButtonClick(GDialogPanel dialogPanel, DialogButton clicked) {
		dialogLayeredPane.remove(dialogPanel);
		dialogLayeredPane.getParent().repaint();
		if (resultListener != null)
			resultListener.OnDialogButtonClick(dialogPanel, clicked);
	}
}

