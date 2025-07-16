package fr.diginamic.gestion_transport.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



/**
 * Utilitaire qui sert simplement à encrypter une chaine de caractères
 */
public class BCryptEncodeMain {

	private static final Logger logger = LoggerFactory.getLogger(BCryptEncodeMain.class);

	public static void encode(String strToEncode) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(strToEncode);
		logger.info("Encoded password: {}", encodedPassword);

	}

}
