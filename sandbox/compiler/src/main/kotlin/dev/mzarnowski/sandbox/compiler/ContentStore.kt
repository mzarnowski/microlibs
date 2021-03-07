package dev.mzarnowski.sandbox.compiler

import java.sql.Connection

class ContentStore(val table: String, val hash: (ByteArray) -> ByteArray) {
    fun create(connection: Connection) {
        val statement = connection.prepareStatement(
            """|CREATE TABLE $table(
               |  hash      CHAR(128) PRIMARY KEY NOT NULL,
               |  content   BLOB NOT NULL
               |)""".trimMargin()
        )

        val foo = statement.executeUpdate()
        print(foo)
    }

    fun load(connection: Connection, hash: ByteArray): ByteArray {
        val results = connection.prepareStatement("SELECT content FROM $table WHERE hash = ?").run {
            setBytes(1, hash)
            executeQuery()
        }

        if (results.next()) return results.getBytes(1)
        else TODO("Hash not present")
    }

    fun store(connection: Connection, content: ByteArray): ByteArray {
        val hash = hash(content)
        val exists = connection.prepareStatement("SELECT EXISTS( SELECT 1 FROM $table WHERE hash = ? LIMIT 1)").run {
            setBytes(1, hash)
            val result = executeQuery()
            result.next() && result.getBoolean(1)
        }

        if (!exists) {
            val inserted = connection.prepareStatement("INSERT INTO $table (hash, content) VALUES(?, ?)").run {
                setBytes(1, hash)
                setBytes(2, content)
                executeUpdate()
            }

            if (inserted != 1) TODO("Could not insert")
        }

        return hash
    }
}