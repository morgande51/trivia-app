package com.nge.triviaapp.domain;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.util.AnnotationLiteral;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

@Entity
@Table(name="contestant", indexes=@Index(unique=true, columnList="email"))
@NamedQueries({
	@NamedQuery(name="Contestant.findFromEmail", query="select c from Contestant c left join fetch c.roles left join fetch c.scores where c.email like :email"),
	@NamedQuery(name="Contestant.findAll", query="select c from Contestant c join fetch c.scores")
})
@JsonbVisibility(FieldVisibilityStrategy.class)
@Data
@EqualsAndHashCode(exclude= {"totalScore", "fullName", "passwordHash", "salt", "scores", "roles"})
@ToString(exclude= {"scores", "roles"})
@Log
public class Contestant implements Principal, ActiveDomain, Serializable {
	
	private static final String SALT_ALG = "SHA1PRNG";
	private static final int SALT_BUFFER_SIZE = 8;
	private static final String PWD_ENCR_ALG = "PBKDF2WithHmacSHA1";
	private static final int DERRIVED_KEY_LENGTH = 160;
	private static final int PASSWORD_ENC_IT = 20000;

	@Id
	@SequenceGenerator(name="contestant_seq_gen", sequenceName="contestant_seq")
	@GeneratedValue(generator="contestant_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="first_name", nullable=false)
	private String firstName;
	
	@Column(name="last_name", nullable=false)
	private String lastName;
	
	@Column(name="email", nullable=false, unique=true)
	private String email;
	
	@JsonbTransient
	@Setter(AccessLevel.NONE)
	@Column(name="password", nullable=false)
	private String passwordHash;
	
	@JsonbTransient
	@Setter(AccessLevel.NONE)
	@Column(name="salt", nullable=false)
	private String salt;
	
	@OneToMany(mappedBy="contestant", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonbTransient
	private Set<ContestantScore> scores;
	
	@JsonbTransient
	@Getter(AccessLevel.NONE)
	@OneToMany(mappedBy="contestant", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<ContestantRole> roles;
	
	@Transient
	@Setter(AccessLevel.NONE)
	private int totalScore;
	
	@Transient
	@Setter(AccessLevel.NONE)
	private String fullName;
	
	@PostLoad
	public void init() {
		fullName = firstName + " " + lastName;
		updateTotalScore();
	}
	
	public void updateScore(Question question) {
		int value = question.getValue();
		ContestantScore scoreData = findScoreFor(question);
	
		if (scoreData == null) {
			scoreData = ContestantScore.newInstance(question.getCategory().getRound(), this);
			if (scores == null) {
				scores = new HashSet<ContestantScore>();
			}
			scores.add(scoreData);
			log.info("Contestant score: " + scoreData);
		}
		int score = scoreData.getScore();
		
		if (question.getAnswerType() == QuestionAnswerType.CORRECT) {
			question.setAnsweredBy(this);
			score += value;
		}
		else if (question.getAnswerType() == QuestionAnswerType.INCORRECT) {
			question.setAnsweredBy(this);
			score -= value;
		}
		
		scoreData.setScore(score);
		updateTotalScore();
	}
	
	protected ContestantScore findScoreFor(Question question) {
		Round targetRound = question.getCategory().getRound();
		ContestantScore score = scores.stream()
				.filter(s -> s.getRound().equals(targetRound) && s.getContestant().equals(this))
				.findAny()
				.orElse(null);
		return score;
	}
	
	protected void updateTotalScore() {
		totalScore = scores.stream().mapToInt(ContestantScore::getScore).sum();
	}
	
	@Override
	public String getName() {
		return email;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <D> D as() {
		return (D) this;
	}
	
	@Override
	public AnnotationLiteral<Active> getLiteral() {
		return new ContestantLiternal();
	}
	
	public Set<String> getRoles() {
		return roles.stream()
				.map(ContestantRole::getRoleName)
				.collect(Collectors.toSet());
	}
	
	public void savePassword(String password) {
		try {
			byte[] salt = generateSalt();
			byte[] pwd = encryptePwd(salt, password);
			this.passwordHash = encode(pwd);
			this.salt = encode(salt);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	public boolean validate(String password) {
		boolean match = false;
		try {
			byte[] pwd = encryptePwd(decode(salt), password);
			match = Arrays.equals(pwd, decode(passwordHash));
			System.out.println("pwd match: " + match);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return match;
	}
	
	protected byte[] generateSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance(SALT_ALG);
		byte[] salt = new byte[SALT_BUFFER_SIZE];
		sr.nextBytes(salt);
		return salt;
	}
	
	protected byte[] encryptePwd(byte[] salt, String password) throws NoSuchAlgorithmException, GeneralSecurityException {
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PASSWORD_ENC_IT, DERRIVED_KEY_LENGTH);
		SecretKeyFactory f = SecretKeyFactory.getInstance(PWD_ENCR_ALG);
		return f.generateSecret(spec).getEncoded();
	}
	
	protected String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}
	
	protected byte[] decode(String str) {
		return Base64.getDecoder().decode(str);
	}
	
	/*
	public static <T extends Principal> Contestant as(T user) {
		return (Contestant) user;
	}
	*/
	
	public static Contestant createFrom(JsonObject userData) {
		Contestant contestant = new Contestant();
		contestant.setEmail(userData.getString("userEmail").toUpperCase());
		contestant.savePassword(userData.getString("userPwd"));
		contestant.setFirstName(userData.getString("firstName", "John"));
		contestant.setLastName(userData.getString("lastName", "Doe"));
		contestant.setRoles(userData.getJsonArray("userRoles")
									.stream()
									.map(r -> ContestantRole.createFrom((JsonString) r, contestant))
									.collect(Collectors.toSet()));
		return contestant;
	}
	
	class ContestantLiternal extends ActiveActionLiteral {
		
		@Override
		public Class<? extends ActiveDomain> value() {
			return Contestant.class;
		}
		
		private static final long serialVersionUID = 1L;
	}
	
	private static final long serialVersionUID = 1L;
}