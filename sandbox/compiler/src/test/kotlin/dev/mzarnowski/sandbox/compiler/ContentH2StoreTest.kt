package dev.mzarnowski.sandbox.compiler

import dev.mzarnowski.sandbox.compiler.ContentStore
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.MessageDigest
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class ContentH2StoreTest {
    init {
        Class.forName("org.h2.Driver")
    }

    private lateinit var connection: Connection

    @BeforeEach
    fun setup_connection() {
        val id = UUID.randomUUID()
        connection = DriverManager.getConnection("jdbc:h2:mem:$id;DB_CLOSE_DELAY=-1")
    }

    @AfterEach
    fun close_connection() {
        connection.close()
    }

    @Test
    fun creates_table() {
        val casql = ContentStore(Table, ::sha512)
        casql.create(connection)
        connection.commit()

        connection.metaData.getTables(null, null, Table, null).apply {
            assertTrue(next())
            assertEquals(Table, getString(3))
        }
    }

    @Test
    fun loads_the_content_matching_its_hash() {
        val casql = ContentStore(Table, ::sha512)
        casql.create(connection)
        connection.commit()

        val fooHash = casql.store(connection, "Foo".toByteArray())
        val barHash = casql.store(connection, "Bar".toByteArray())
        connection.commit()

        assertArrayEquals("Foo".toByteArray(), casql.load(connection, fooHash))
        assertArrayEquals("Bar".toByteArray(), casql.load(connection, barHash))
    }

    @Test
    fun inserts_same_content_only_once() {
        val casql = ContentStore(Table, ::sha512)
        casql.create(connection)
        connection.commit()

        val content = "Hello".toByteArray()
        repeat(10) { casql.store(connection, content) }
        connection.commit()

        connection.createStatement().executeQuery("SELECT COUNT(*) FROM $Table").apply {
            assertTrue(next())
            assertEquals(1, getInt(1))
        }
    }
}

private const val Table = "CAS"
private fun sha512(it: ByteArray) = MessageDigest.getInstance("SHA512").digest(it)