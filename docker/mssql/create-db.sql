USE master
GO
IF EXISTS(SELECT * FROM sys.databases WHERE name='jodd_test')
	DROP DATABASE jodd_test
GO
CREATE DATABASE jodd_test
GO