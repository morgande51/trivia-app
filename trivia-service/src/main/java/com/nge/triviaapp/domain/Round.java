package com.nge.triviaapp.domain;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.JsonObject;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="round")
@NamedQueries({
	@NamedQuery(name="Rounds.findAll", query="select r from Round r")
})
@JsonbVisibility(FieldVisibilityStrategy.class)
@Data
@EqualsAndHashCode(exclude="categories")
@ToString(exclude="categories")
public class Round implements ActiveDomain, Serializable {
	
	@Id
	@SequenceGenerator(name="round_seq_gen", sequenceName="round_seq")
	@GeneratedValue(generator="round_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="round_name")
	private String name;
	
	@OneToMany(mappedBy="round", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonbTransient
	private Set<Category> categories;
	
	@SuppressWarnings("unchecked")
	public <D> D as() {
		return (D) this;
	}
	
	public ActiveActionLiteral getLiteral() {
		return new RoundLiteral();
	}
	
	public static Round createFrom(JsonObject jsonData) {
		Round round = new Round();
		round.setName(jsonData.getString("roundName"));
		Set<Category> categories = jsonData.getJsonArray("categories").stream().map(c -> Category.createFrom(c.asJsonObject(), round)).collect(Collectors.toSet());
		round.setCategories(categories);
		return round;
	}
	
	class RoundLiteral extends ActiveActionLiteral {

		@Override
		public Class<? extends ActiveDomain> value() {
			return Round.class;
		}
		
		private static final long serialVersionUID = 1L;
	}
	
	private static final long serialVersionUID = 1L;
}