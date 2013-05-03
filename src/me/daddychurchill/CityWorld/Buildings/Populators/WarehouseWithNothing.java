package me.daddychurchill.CityWorld.Buildings.Populators;

import me.daddychurchill.CityWorld.Buildings.Rooms.EmptyRoom;
import me.daddychurchill.CityWorld.Buildings.Rooms.StorageDoubleShelvesRoom;
import me.daddychurchill.CityWorld.Plugins.RoomProvider;

public class WarehouseWithNothing extends RoomProvider  {

	public WarehouseWithNothing() {
		super();

		roomTypes.add(new EmptyRoom());
		roomTypes.add(new StorageDoubleShelvesRoom());
	}

}
