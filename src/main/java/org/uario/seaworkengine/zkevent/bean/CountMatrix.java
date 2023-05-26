package org.uario.seaworkengine.zkevent.bean;

/**
 * @author francesco Count Matrix for info about users working
 */
public class CountMatrix {

	private Integer[] count_Day_Users;

	private Double[][] count_matrix;

	private Double[] count_matrix_row;

	private Integer[][] count_matrixUsers;

	public Integer[] getCount_Day_Users() {
		return this.count_Day_Users;
	}

	public Double[][] getCount_matrix() {
		return this.count_matrix;
	}

	public Double[] getCount_matrix_row() {
		return this.count_matrix_row;
	}

	public Integer[][] getCount_matrixUsers() {
		return this.count_matrixUsers;
	}

	public void setCount_Day_Users(Integer[] count_Day_Users) {
		this.count_Day_Users = count_Day_Users;
	}

	public void setCount_matrix(Double[][] count_matrix) {
		this.count_matrix = count_matrix;
	}

	public void setCount_matrix_row(Double[] count_matrix_row) {
		this.count_matrix_row = count_matrix_row;
	}

	public void setCount_matrixUsers(Integer[][] count_matrixUsers) {
		this.count_matrixUsers = count_matrixUsers;
	}

}
