/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.animations;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;

/**
 *
 * @author Richard
 */
public class SpinAnimation extends AnimationTimer {

	private Node node;
	private double angle = 360;
	private double spinDelta = 5;

	public SpinAnimation() {
	}

	public void setSpinDelta(long delta) {
		spinDelta = delta;
	}

	public void setNode(Node node) {
		this.node = node;
		node.setRotate(angle);
	}

	public void reset() {
		angle = 0;
		node.setRotate(angle);
	}

	@Override
	public void handle(long now) {
		angle = angle - spinDelta;

		if (angle <= 0) {
			angle = 360;
		}

		node.setRotate(angle);
	}

}
