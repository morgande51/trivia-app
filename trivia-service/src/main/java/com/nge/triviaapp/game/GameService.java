package com.nge.triviaapp.game;

import java.util.Set;

import com.nge.triviaapp.domain.Category;
import com.nge.triviaapp.domain.Question;

public interface GameService {

	public Set<Category> getActiveRoundCategories();

	public Set<Question> getActiveRoundCategoryQuestions(long categoryId);

	public void makeQuestionActive(QuestionSelectionRequest request);

}
