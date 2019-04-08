package r01f.persistence.search.db;

import javax.persistence.EntityManager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.model.search.SearchFilter;
import r01f.patterns.IsBuilder;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.config.DBModuleConfig;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DBSearchQueryBuilder
  		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	public static <DB extends DBEntity> DBSearchQueryBuilderDBModuleConfigStep<DB> forDBEntityType(final Class<DB> dbEntityType) {
		return new DBSearchQueryBuilder() { /* nothing */ }
					.new DBSearchQueryBuilderDBModuleConfigStep<DB>(dbEntityType);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class DBSearchQueryBuilderDBModuleConfigStep<DB extends DBEntity> {
		private final Class<DB> _dbEntityType;
		public DBSearchQueryBuilderEntityManagerStep<DB> usingDBModuleConfig(final DBModuleConfig dbModuleConfig) {
			return new DBSearchQueryBuilderEntityManagerStep<DB>(_dbEntityType,					
															  dbModuleConfig);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class DBSearchQueryBuilderEntityManagerStep<DB extends DBEntity> {
		private final Class<DB> _dbEntityType;
		private final DBModuleConfig _dbModuleConfig;
		public DBSearchBuilderQueryUILanguageStep<DB> with(final EntityManager entityManager) {
			return new DBSearchBuilderQueryUILanguageStep<DB>(_dbEntityType,
															  _dbModuleConfig,
															  entityManager);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class DBSearchBuilderQueryUILanguageStep<DB extends DBEntity> {
		private final Class<DB> _dbEntityType;
		private final DBModuleConfig _dbModuleConfig;
		private final EntityManager _entityManager;	
		
		public DBSearchQueryBuilderPredicatesStep<DB> withUILanguage(final Language uiLang) {
			return new DBSearchQueryBuilderPredicatesStep<DB>(_dbEntityType,
															  _dbModuleConfig,
															  _entityManager,
															  uiLang);
		}
		public DBSearchQueryBuilderPredicatesStep<DB> noUILanguage() {
			return new DBSearchQueryBuilderPredicatesStep<DB>(_dbEntityType,
															  _dbModuleConfig,
															  _entityManager,
															  null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class DBSearchQueryBuilderPredicatesStep<DB extends DBEntity> {
		private final Class<DB> _dbEntityType;
		private final DBModuleConfig _dbModuleConfig;
		private final EntityManager _entityManager;
		private final Language _uiLanguage;
		
		public <F extends SearchFilter> DBSearchQuery<F,DB> withFilterType(final Class<F> filterType) {
			DBSearchQuery<F,DB> outQuery = new DBSearchQuery<F,DB>(_dbEntityType,
															       _dbModuleConfig,
															       _entityManager,
													   		       _uiLanguage);
			return outQuery;
		}
	}
}
