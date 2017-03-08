package com.wallet.utils.misc;

public class SqlUtils {
	/* MySql error codes */
	/**
	 * Error: 1062 SQLSTATE: 23000 (ER_DUP_ENTRY)
	 * Message: Duplicate entry '%s' for key %d
	 */
	public static final int ER_DUP_ENTRY = 1062;
	/**
	 * Error: 1050 SQLSTATE: 42S01 (ER_TABLE_EXISTS_ERROR)
	 * Message: Table '%s' already exists
	 */
	
	public static final int ER_TABLE_EXISTS_ERROR = 1050;
}
