import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Hash256 {
    fun hashPassword(password: String): String {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashedBytes = digest.digest(password.toByteArray())
            val hashedPassword = StringBuilder()
            for (byte in hashedBytes) {
                hashedPassword.append(String.format("%02x", byte))
            }
            return hashedPassword.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return ""
        }
    }
}