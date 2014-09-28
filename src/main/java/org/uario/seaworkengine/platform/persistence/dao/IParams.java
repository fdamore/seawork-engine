/**
 *
 */
package org.uario.seaworkengine.platform.persistence.dao;

/**
 * @author francesco
 *
 */
public interface IParams {

	/**
	 * Get param
	 *
	 * @param name
	 * @return
	 */
	public String getParam(String name);

	/**
	 * Get param
	 *
	 * @param name
	 * @return
	 */
	public void setParam(String name, String val);

}
