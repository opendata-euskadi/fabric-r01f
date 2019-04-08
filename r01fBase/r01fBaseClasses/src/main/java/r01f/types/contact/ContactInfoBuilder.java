package r01f.types.contact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.IsBuilder;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class ContactInfoBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final ContactInfo _contactInfo;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactInfoBuilderVisibilityStep create() {
		return new ContactInfoBuilder(new ContactInfo())
						.new ContactInfoBuilderVisibilityStep();
	}
	public static ContactInfoBuilderPhonesStep createHidden() {
		return ContactInfoBuilder.create().hidden();
	}
	public static ContactInfoBuilderPhonesStep createVisible() {
		return ContactInfoBuilder.create().visible();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderVisibilityStep {
		public ContactInfoBuilderPhonesStep visible() {
			_contactInfo.setPrivate(false);
			return new ContactInfoBuilderPhonesStep();
		}
		public ContactInfoBuilderPhonesStep hidden() {
			_contactInfo.setPrivate(true);
			return new ContactInfoBuilderPhonesStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderPhonesStep {
		public ContactInfoBuilderPhonesStep addPhone(final ContactPhone... phones) {
			for (ContactPhone phone : phones) _contactInfo.addPhone(phone);
			return this;
		}
		public ContactInfoBuilderMailsStep noPhones() {
			return new ContactInfoBuilderMailsStep();
		}
		public ContactInfoBuilderWebsStep noMail() {
			return new ContactInfoBuilderWebsStep();
		}
		public ContactInfoBuilderMailsStep addMail(final ContactMail... mails) {
			return new ContactInfoBuilderMailsStep()
							.addMail(mails);
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderMailsStep {
		public ContactInfoBuilderMailsStep addMail(final ContactMail... mails) {
			for (ContactMail mail : mails) _contactInfo.addMailAddress(mail);
			return this;
		}
		public ContactInfoBuilderWebsStep noMail() {
			return new ContactInfoBuilderWebsStep();
		}
		public ContactInfoBuilderSocialNetworksStep noWeb() {
			return new ContactInfoBuilderSocialNetworksStep();
		}
		public ContactInfoBuilderWebsStep addWeb(final ContactWeb... webs) {
			return new ContactInfoBuilderWebsStep()
							.addWeb(webs);
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderWebsStep {
		public ContactInfoBuilderWebsStep addWeb(final ContactWeb... webs) {
			for (ContactWeb web : webs) _contactInfo.addWebSite(web);
			return this;
		}
		public ContactInfoBuilderSocialNetworksStep noWeb() {
			return new ContactInfoBuilderSocialNetworksStep();
		}
		public ContactInfoBuilderBuildStep noSocialNetwork() {
			return new ContactInfoBuilderBuildStep();
		}
		public ContactInfoBuilderSocialNetworksStep addSocialNetwork(final ContactSocialNetwork... socNets) {
			return new ContactInfoBuilderSocialNetworksStep()
							.addSocialNetwork(socNets);
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderSocialNetworksStep {
		public ContactInfoBuilderSocialNetworksStep addSocialNetwork(final ContactSocialNetwork... socNets) {
			for (ContactSocialNetwork socNet : socNets) _contactInfo.addSocialNetwork(socNet);
			return this;
		}
		public ContactInfoBuilderBuildStep noSocialNetwork() {
			return new ContactInfoBuilderBuildStep();
		}
		public ContactInfoBuilderBuildStep contactIn(final Language lang) {
			return new ContactInfoBuilderPreferedLanguageStep()
							.contactIn(lang);
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderPreferedLanguageStep {
		public ContactInfoBuilderBuildStep contactIn(final Language lang) {
			_contactInfo.setPreferedLanguage(lang);
			return new ContactInfoBuilderBuildStep();
		}
		public ContactInfoBuilderBuildStep noPreferedLanguage() {
			return new ContactInfoBuilderBuildStep();
		}
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ContactInfoBuilderBuildStep {
		public ContactInfo build() {
			return _contactInfo;
		}
	}
}
