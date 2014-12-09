package org.uario.seaworkengine.platform.persistence.dao;

import org.uario.seaworkengine.model.Ship;

public interface IShip {

	public void createShip(Ship ship);

	public void deleteShip(Integer id_ship);

	public Ship loadShip(Integer id_ship);

	public void updateShip(Ship ship);

}
