package com.jcodes.hessian;

import java.io.Serializable;

public class Car implements Serializable {

	private static final long serialVersionUID = 4736905401908455439L;

	private String carName;

	private String carModel;

	/**
	 * @return the carName
	 */
	public String getCarName() {
		return carName;
	}

	/**
	 * @return the carModel
	 */
	public String getCarModel() {
		return carModel;
	}

	/**
	 * @param pCarName
	 *            the carName to set
	 */
	public void setCarName(String pCarName) {
		carName = pCarName;
	}

	/**
	 * @param pCarModel
	 *            the carModel to set
	 */
	public void setCarModel(String pCarModel) {
		carModel = pCarModel;
	}

	/**
	 * @see java.lang.Object#toString()
	 * @return
	 */
	@Override
	public String toString() {
		return "my car name:[" + this.carName + "] model:[" + this.carModel + "].";
	}

}