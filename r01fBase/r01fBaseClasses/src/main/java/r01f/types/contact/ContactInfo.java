package r01f.types.contact;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.guids.CommonOIDs.UserCode;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoFacets.HasGeoPosition;
import r01f.types.geo.GeoPosition;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

/**
 * Contact info
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="contactInfo")
@Accessors(prefix="_")
@NoArgsConstructor
public class ContactInfo
     extends ContactInfoBase<ContactInfo>
  implements HasGeoPosition {

	private static final long serialVersionUID = 8960930452114541680L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Phones
	 */
	@MarshallField(as="phoneChannels")
	@Getter @Setter private Collection<ContactPhone> _contactPhones;
	/**
	 * Social network users
	 */
	@MarshallField(as="socialNetworkChannels")
	@Getter @Setter private Collection<ContactSocialNetwork> _contactSocialNetworks;
	/**
	 * email
	 */
	@MarshallField(as="emailChannels")
	@Getter @Setter private Collection<ContactMail> _contactMailAddresses;
	/**
	 * web sites
	 */
	@MarshallField(as="webSiteChannels")
	@Getter @Setter private Collection<ContactWeb> _contactWebSites;
	/**
	 * The language the user prefers to be used in the interaction with him/her
	 */
	@MarshallField(as="preferredLanguage")
	@Getter @Setter private Language _preferedLanguage;
	/**
	 * The geo-position of user.
	 */
	@MarshallField(as="geoPosition")
	@Getter @Setter private GeoPosition _geoPosition;
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactInfo create() {
		ContactInfo outContact = new ContactInfo();
		return outContact;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	MAIL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are mail address associated with the contact info
	 */
	public boolean hasMailAddresses() {
		return CollectionUtils.hasData(_contactMailAddresses);
	}
	/**
	 * Check if there exists a {@link ContactMail} with the given email
	 * @param theEmail
	 * @return
	 */
	public boolean hasMmail(final EMail theEmail) {
		EMail searchedMail = Iterables.tryFind(this.getMailAddresses(),
												new Predicate<EMail>() {
														@Override
														public boolean apply(final EMail aMail) {
															return aMail.equals(theEmail);
												}})
         							   .orNull();
		return searchedMail != null;
	}
	/**
	 * Returns the first {@link ContactMail} with the given email
	 * @param email
	 * @return
	 */
	public ContactMail getMailFor(final EMail email) {
		if (CollectionUtils.isNullOrEmpty(_contactMailAddresses)) return null;
		return FluentIterable.from(_contactMailAddresses)
							 .firstMatch(new Predicate<ContactMail>() {
												@Override
												public boolean apply(final ContactMail mail) {
													return mail != null
														&& mail.getMail().equals(email);
												}
										 })
							 .orNull();	
	}
	/**
	 * Returns an email address for an intended usage
	 * @param id
	 * @return
	 */
	public EMail getMailAddress(final ContactInfoUsage usage) {
		ContactMail mail = _find(_contactMailAddresses,usage);
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns an email addess for an intended usage or any if the requested one
	 * does not exists
	 * @param usage
	 * @return
	 */
	public EMail getMailAddressOrAny(final ContactInfoUsage usage) {
		EMail outMail = this.getMailAddress(usage);
		if (outMail == null && CollectionUtils.hasData(_contactMailAddresses)) {
			ContactMail mail = CollectionUtils.pickOneElement(_contactMailAddresses);
			if (mail != null) outMail = mail.getMail();
		}
		return outMail;
	}
	/**
	 * Returns the default email address
	 * @return
	 */
	public EMail getDefaultMailAddress() {
		ContactMail mail = _findDefault(_contactMailAddresses);
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns the default email address or any of them if none is set as default one
	 * @return
	 */
	public EMail getDefaultMailAddressOrAny() {
		ContactMail mail = _findDefault(_contactMailAddresses);
		if (mail == null && CollectionUtils.hasData(_contactMailAddresses)) mail = CollectionUtils.pickOneElement(_contactMailAddresses);
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns a mail address other than default
	 * @return
	 */
	public EMail getMailAddressOtherThanDefaul() {
		ContactMail mail = _findOtherThanDefault(_contactMailAddresses);
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns a mail address (if there is more than one it returns one of them randomly)
	 * @return
	 */
	public EMail getMailAddress() {
		// Try to find the default
		EMail mail = this.getDefaultMailAddress();
		// ... if there's NO default, return any of them
		if (mail == null) {
			ContactMail contactMail = _findOne(_contactMailAddresses);
			mail = contactMail != null ? contactMail.getMail() : null;
		}
		return mail;
	}
	/**
	 * Returns a {@link Collection} of {@link EMail}s
	 * @return
	 */
	public Collection<EMail> getMailAddresses() {
		Collection<EMail> mails = CollectionUtils.hasData(_contactMailAddresses)
										? FluentIterable.from(_contactMailAddresses)
														.filter(new Predicate<ContactMail>() {
																	@Override
																	public boolean apply(final ContactMail contactMail) {
																		return contactMail.getMail() != null;
																	}
																})
														.transform(new Function<ContactMail,EMail>() {
																		@Override
																		public EMail apply(final ContactMail contactMail) {
																			return contactMail.getMail();
																		}
															     })
														.toList()
										 : null;
		return mails;
	}
	/**
	 * Returns the mail addresses as a comma separated list
	 * @param separator
	 * @return
	 */
	public String getMailAddresesCharSeparated(final char separator) {
		Collection<EMail> mails = this.getMailAddresses();
		return CollectionUtils.hasData(mails)
					? CollectionUtils.of(mails)
									 .toStringSeparatedWith(separator)
				    : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PHONE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are phones associated with the contact info
	 */
	public boolean hasPhones() {
		return CollectionUtils.hasData(_contactPhones);
	}
	/**
	 * Checks if there exists a {@link ContactPhone} with the given number
	 * @param thePhone
	 * @return
	 */
	public boolean hasPhone(final Phone thePhone) {
		Phone searchedPhone = Iterables.tryFind(this.getPhones(),
												new Predicate<Phone>() {
														@Override
														public boolean apply(final Phone aPhone) {
															return aPhone.equals(thePhone);
												}})
         							   .orNull();
		return searchedPhone != null;
	}
	/**
	 * Returns the first {@link ContactPhone} with the given number
	 * @param phone
	 * @return
	 */
	public ContactPhone getPhoneFor(final Phone phone) {
		if (CollectionUtils.isNullOrEmpty(_contactPhones)) return null;
		return FluentIterable.from(_contactPhones)
							 .firstMatch(new Predicate<ContactPhone>() {
												@Override
												public boolean apply(final ContactPhone aPhone) {
													return aPhone != null
														&& aPhone.getNumber().equals(phone);
												}
										 })
							 .orNull();	
	}
	/**
	 * Returns a phone for an intended usage
	 * @param id
	 * @return
	 */
	public Phone getPhone(final ContactInfoUsage usage) {
		ContactPhone phone = _find(_contactPhones,usage);
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns a phone of type.
	 * @param id
	 * @return
	 */
	public Phone getPhone(final ContactPhoneType type) {
		ContactPhone phone = _find(_contactPhones,type);
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns the default phone number
	 * @return
	 */
	public Phone getDefaultPhone() {
		ContactPhone phone = _findDefault(_contactPhones);
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns the default phone number or any of them if any is set as the default one
	 * @return
	 */
	public Phone getDefaultPhoneOrAny() {
		ContactPhone phone = _findDefault(_contactPhones);
		if (phone == null && CollectionUtils.hasData(_contactPhones)) phone = CollectionUtils.pickOneElement(_contactPhones);
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns any not-default phone number
	 * @return
	 */
	public Phone getPhoneOtherThanDefault() {
		ContactPhone phone = _findOtherThanDefault(_contactPhones);
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns a phone number (if there is more than one it returns one of them randomly)
	 * @return
	 */
	public Phone getPhone() {
		// Try to find the default
		Phone phone = this.getDefaultPhone();
		// ... if there's NO default, return any of them
		if (phone == null) {
			ContactPhone contactPhone = _findOne(_contactPhones);
			phone = contactPhone != null ? contactPhone.getNumber() : null;
		}
		return phone;
	}
	/**
	 * Returns a {@link Collection} of {@link Phone} numbers
	 * @return
	 */
	public Collection<Phone> getPhones() {
		Collection<Phone> phones = CollectionUtils.hasData(_contactPhones)
										? FluentIterable.from(_contactPhones)
														.filter(new Predicate<ContactPhone>() {
																		@Override
																		public boolean apply(final ContactPhone contactPhone) {
																			return contactPhone.getNumber() != null;
																		}
																})
													    .transform(new Function<ContactPhone,Phone>() {
																		@Override
																		public Phone apply(final ContactPhone contactPhone) {
																			return contactPhone.getNumber();
																		}
															       })
													    .toList()
										 : null;
		return phones;
	}
	/**
	 * Returns the mail addresses as a comma separated list
	 * @param separator
	 * @return
	 */
	public String getPhonesCharSeparated(final char separator) {
		Collection<Phone> phones = this.getPhones();
		return CollectionUtils.hasData(phones)
					? CollectionUtils.of(phones)
									 .toStringSeparatedWith(separator)
				    : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	SOCIAL NETWORK
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are social network associated with the contact info
	 */
	public boolean hasSocialNetworks() {
		return CollectionUtils.hasData(_contactSocialNetworks);
	}
	public boolean hasSocialNetwork(final ContactSocialNetworkType type,
									final UserCode user) {
		ContactSocialNetwork searchedNet = Iterables.tryFind(_contactSocialNetworks,
															 new Predicate<ContactSocialNetwork>() {
																	@Override
																	public boolean apply(final ContactSocialNetwork socialNet) {
																		return socialNet != null
																			&& socialNet.getType() == type
																			&& socialNet.getUser().is(user);
															 }})
			         							   .orNull();
		return searchedNet != null;
	}
	/**
	 * Returns the first {@link ContactSocialNetwork} with the given type for the given user
	 * @param type
	 * @param user
	 * @return
	 */
	public ContactSocialNetwork getSocialNetworkFor(final ContactSocialNetworkType type,
													final UserCode user) {
		if (CollectionUtils.isNullOrEmpty(_contactSocialNetworks)) return null;
		return FluentIterable.from(_contactSocialNetworks)
							 .firstMatch(new Predicate<ContactSocialNetwork>() {
												@Override
												public boolean apply(final ContactSocialNetwork socialNet) {
													return socialNet != null
														&& socialNet.getType() == type
														&& socialNet.getUser().is(user);
												}
										 })
							 .orNull();	
	}
	/**
	 * Returns a social network for an intended usage
	 * @param type
	 * @param usage
	 * @return
	 */
	public ContactSocialNetwork getSocialNetwork(final ContactSocialNetworkType type,
								  				 final ContactInfoUsage usage) {
		ContactSocialNetwork net = CollectionUtils.hasData(_contactSocialNetworks) 
										? CollectionUtils.of(_contactSocialNetworks)
												     	 .findFirstElementMatching(new Predicate<ContactSocialNetwork>() {
																							@Override
																							public boolean apply(final ContactSocialNetwork el) {
																								return el.getUsage() == usage
																									&& el.getType() == type;
																							}
													  							    })
										: null;
		return net;
	}
	/**
	 * Returns the default social network
	 * @return
	 */
	public ContactSocialNetwork getDefaultSocialNetwork() {
		ContactSocialNetwork net = _findDefault(_contactSocialNetworks);
		return net;
	}
	/**
	 * Returns the default social network or any of them if any is set as the default one
	 * @return
	 */
	public ContactSocialNetwork getDefaultSocialNetworkOrAny() {
		ContactSocialNetwork net = _findDefault(_contactSocialNetworks);
		if (_contactSocialNetworks == null && CollectionUtils.hasData(_contactSocialNetworks)) net = CollectionUtils.pickOneElement(_contactSocialNetworks);
		return net;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	WEB
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are web site associated with the contact info
	 */
	public boolean hasWebSites() {
		return CollectionUtils.hasData(_contactWebSites);
	}
	/**
	 * Check if there exists a {@link ContactWeb} with the given email
	 * @param theSiteUrl
	 * @return
	 */
	public boolean hasWebSite(final Url theSiteUrl) {
		Url searchedUrl = Iterables.tryFind(this.getWebUrls(),
												new Predicate<Url>() {
														@Override
														public boolean apply(final Url url) {
															return url.equals(theSiteUrl);
												}})
         							   .orNull();
		return searchedUrl != null;
	}
	/**
	 * Returns the first {@link ContactWeb} with the given url
	 * @param url
	 * @return
	 */
	public ContactWeb getMailFor(final Url url) {
		if (CollectionUtils.isNullOrEmpty(_contactWebSites)) return null;
		return FluentIterable.from(_contactWebSites)
							 .firstMatch(new Predicate<ContactWeb>() {
												@Override
												public boolean apply(final ContactWeb web) {
													return web != null
														&& web.getUrl().equals(url);
												}
										 })
							 .orNull();	
	}
	/**
	 * Returns an url for an intended usage
	 * @param usage
	 * @return
	 */
	public Url getWebUrl(final ContactInfoUsage usage) {
		ContactWeb web = _find(_contactWebSites,usage);
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns an url for an intended usage or any if the requested one
	 * does not exists
	 * @param usage
	 * @return
	 */
	public Url getWebUrlOrAny(final ContactInfoUsage usage) {
		Url outUrl = this.getWebUrl(usage);
		if (outUrl == null && CollectionUtils.hasData(_contactWebSites)) {
			ContactWeb url = CollectionUtils.pickOneElement(_contactWebSites);
			if (url != null) outUrl = url.getUrl();
		}
		return outUrl;
	}
	/**
	 * Returns the default url address
	 * @return
	 */
	public Url getDefaultWebUrl() {
		ContactWeb web = _findDefault(_contactWebSites);
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns the default url or any of them if none is set as default one
	 * @return
	 */
	public Url getDefaultWebUrlOrAny() {
		ContactWeb web = _findDefault(_contactWebSites);
		if (web == null && CollectionUtils.hasData(_contactWebSites)) web = CollectionUtils.pickOneElement(_contactWebSites);
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns a web url other than default
	 * @return
	 */
	public Url getWebUrlOtherThanDefaul() {
		ContactWeb web = _findOtherThanDefault(_contactWebSites);
		return web != null ? web.getUrl() : null;
	}
	/**
	 * Returns a web url (if there is more than one it returns one of them randomly)
	 * @return
	 */
	public Url getWebUrl() {
		// Try to find the default
		Url defUrl = this.getDefaultWebUrl();
		// ... if there's NO default, return any of them
		if (defUrl == null) {
			ContactWeb contactWeb = _findOne(_contactWebSites);
			defUrl = contactWeb != null ? contactWeb.getUrl() : null;
		}
		return defUrl;
	}
	/**
	 * Returns a {@link Collection} of {@link Url}s
	 * @return
	 */
	public Collection<Url> getWebUrls() {
		Collection<Url> urls = CollectionUtils.hasData(_contactWebSites)
										? FluentIterable.from(_contactWebSites)
														.filter(new Predicate<ContactWeb>() {
																	@Override
																	public boolean apply(final ContactWeb web) {
																		return web.getUrl() != null;
																	}
																})
														.transform(new Function<ContactWeb,Url>() {
																		@Override
																		public Url apply(final ContactWeb web) {
																			return web.getUrl();
																		}
															     })
														.toList()
										 : null;
		return urls;
	}
	/**
	 * Returns the web urls as a comma separated list
	 * @param separator
	 * @return
	 */
	public String getWebUrlsCharSeparated(final char separator) {
		Collection<Url> urls = this.getWebUrls();
		return CollectionUtils.hasData(urls)
					? CollectionUtils.of(urls)
									 .toStringSeparatedWith(separator)
				    : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfo addPhone(final ContactPhone phone) {
		if (_contactPhones == null) _contactPhones = Lists.newArrayList();
		_contactPhones.add(phone);
		return this;
	}
	public ContactInfo addPhones(final Collection<ContactPhone> phones) {
		if (_contactPhones == null) _contactPhones = Lists.newArrayList();
		if (CollectionUtils.hasData(phones)) _contactPhones.addAll(phones);
		return this;
	}
	public ContactPhone removePhone(final ContactInfoUsage usage) {
		ContactPhone outPhone = _find(_contactPhones,usage);
		if (outPhone != null) _contactPhones.remove(outPhone);
		return outPhone;
	}
	public ContactInfo addSocialNetwork(final ContactSocialNetwork net) {
		if (_contactSocialNetworks == null) _contactSocialNetworks = Lists.newArrayList();
		_contactSocialNetworks.add(net);
		return this;
	}
	public ContactInfo addSocialNetworks(final Collection<ContactSocialNetwork> nets) {
		if (_contactSocialNetworks == null) _contactSocialNetworks = Lists.newArrayList();
		if (CollectionUtils.hasData(nets)) _contactSocialNetworks.addAll(nets);
		return this;
	}
	public ContactSocialNetwork removeSocialNetwork(final ContactInfoUsage id) {
		ContactSocialNetwork outNet = _find(_contactSocialNetworks,id);
		if (outNet != null) _contactSocialNetworks.remove(outNet);
		return outNet;
	}
	public ContactInfo addMailAddress(final ContactMail mail) {
		if (_contactMailAddresses == null) _contactMailAddresses = Lists.newArrayList();
		_contactMailAddresses.add(mail);
		return this;
	}
	public ContactInfo addMailAddress(final Collection<ContactMail> mails) {
		if (_contactMailAddresses == null) _contactMailAddresses = Lists.newArrayList();
		if (CollectionUtils.hasData(mails)) _contactMailAddresses.addAll(mails);
		return this;
	}
	public ContactMail removeMailAddress(final ContactInfoUsage usage) {
		ContactMail outMail = _find(_contactMailAddresses,usage);
		if (outMail != null) _contactMailAddresses.remove(outMail);
		return outMail;
	}
	public ContactInfo addWebSite(final ContactWeb web) {
		if (_contactWebSites == null) _contactWebSites = Lists.newArrayList();
		_contactWebSites.add(web);
		return this;
	}
	public ContactInfo addWebSites(final Collection<ContactWeb> webs) {
		if (_contactWebSites == null) _contactWebSites = Lists.newArrayList();
		if (CollectionUtils.hasData(webs)) _contactWebSites.addAll(webs);
		return this;
	}
	public ContactWeb removeWebSite(final ContactInfoUsage usage) {
		ContactWeb outWeb = _find(_contactWebSites,usage);
		if (outWeb != null) _contactWebSites.remove(outWeb);
		return outWeb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private static <M extends ContactMeanDataBase<?>> M _find(final Collection<? extends ContactMeanDataBase<?>> col,
													 		   final ContactInfoUsage usage) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(new Predicate<ContactMeanDataBase<?>>() {
																									@Override
																									public boolean apply(final ContactMeanDataBase<?> el) {
																										return el.getUsage() == usage;
																									}
															  							    });
		return out;
	}

	@SuppressWarnings("unchecked")
	private static <M extends ContactPhone> M _find(final Collection<? extends ContactPhone> col,
													final ContactPhoneType type) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(new Predicate<ContactPhone>() {
																									@Override
																									public boolean apply(final ContactPhone el) {
																										return el.getType() == type;
																									}
															  							    });
		return out;
	}

	@SuppressWarnings("unchecked")
	private static <M extends ContactMeanDataBase<?>> M _findDefault(final Collection<? extends ContactMeanDataBase<?>> col) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(new Predicate<ContactMeanDataBase<?>>() {
																									@Override
																									public boolean apply(final ContactMeanDataBase<?> el) {
																										return el.isDefault();
																									}
															  							    });
		return out;
	}
	@SuppressWarnings("unchecked")
	private static <M extends ContactMeanDataBase<?>> M _findOne(final Collection<? extends ContactMeanDataBase<?>> col) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
																  .pickOneElement();
		return out;
	}

	@SuppressWarnings("unchecked")
	private static <M extends ContactMeanDataBase<?>> M _findOtherThanDefault(final Collection<? extends ContactMeanDataBase<?>> col) {
		M out = null;
		if (CollectionUtils.hasData(col)) out = (M)CollectionUtils.of(col)
														     	  .findFirstElementMatching(new Predicate<ContactMeanDataBase<?>>() {
																									@Override
																									public boolean apply(final ContactMeanDataBase<?> el) {
																										return !el.isDefault();
																									}
															  							    });
		return out;
	}




}
