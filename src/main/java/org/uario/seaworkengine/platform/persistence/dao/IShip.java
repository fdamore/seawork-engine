package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.Ship;

public interface IShip {

	public void createShip(Ship ship);

	public void deleteShip(Integer id_ship);

	public List<Ship> listAllShip(String full_text_search);

	public List<Ship> loadAllShip();

	public Ship loadShip(Integer id_ship);

	public void updateShip(Ship ship);

}
