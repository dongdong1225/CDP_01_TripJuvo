package com.knucapstone.tripjuvo.database;

import com.knucapstone.tripjuvo.database.data.Data;


public interface DatabaseCallListener
{
	void onDatabaseCallRespond(DatabaseCallTask task, Data<?> data);
	void onDatabaseCallFail(DatabaseCallTask task, Exception exception);
}
