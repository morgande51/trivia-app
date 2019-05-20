package com.nge.triviaapp.contestant;

import java.io.Serializable;

import javax.enterprise.util.AnnotationLiteral;
import javax.json.bind.annotation.JsonbTransient;

import com.nge.triviaapp.domain.QuestionAnswerType;

import lombok.Getter;

@Getter
public class BuzzerResetRequest implements Serializable {

	private QuestionAnswerType answerType;
	
	private boolean adminReset;
	
	@JsonbTransient
	private BuzzerResetLiteral literal;
	
	/**
	 * Constructor
	 * @param answerType The answerType to set
	 */
	public BuzzerResetRequest(QuestionAnswerType answerType) {
		super();
		this.answerType = answerType;
		adminReset = false;
		literal = new BuzzerResetLiteral(adminReset);
	}
	
	/**
	 * Constructor
	 * Crates a reset request with adminReset to true
	 */
	public BuzzerResetRequest() {
		super();
		adminReset = true;
		literal = new BuzzerResetLiteral(adminReset);
	}
	
	class BuzzerResetLiteral extends AnnotationLiteral<BuzzerReset> implements BuzzerReset {
		
		private Boolean adminFlg;
		
		protected BuzzerResetLiteral(boolean adminFlg) {
			this.adminFlg = adminFlg;
		}

		public boolean admin() {
			return adminFlg;
		}
		
		private static final long serialVersionUID = 1L;
	}

	private static final long serialVersionUID = 1L;
}
