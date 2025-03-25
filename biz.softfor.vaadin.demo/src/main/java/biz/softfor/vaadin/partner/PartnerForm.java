package biz.softfor.vaadin.partner;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.Postcode_;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationType_;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerDetails_;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.Labeled;
import biz.softfor.util.StringUtil;
import biz.softfor.util.partner.PartnerType;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.address.PostcodesDbGrid;
import biz.softfor.vaadin.field.ManyToOneField;
import biz.softfor.vaadin.field.ToManyField;
import biz.softfor.vaadin.user.UserGridFieldColumns;
import biz.softfor.vaadin.user.UsersDbGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerForm extends EntityForm<Long, Partner, PartnerWor> {

  private final ComboBox<PartnerType> partnerType;
  private final TextField name;
  private final TextField fullname;
  private final TextField regcode;
  private final DatePicker regdate;
  private final TextField middlename;
  private final TextField passportSeries;
  private final IntegerField passportNumber;
  private final DatePicker passportDate;
  private final TextArea passportIssued;
  private final Checkbox married;
  private final TextField address;
  private final ManyToOneField<Long, Partner> parentField;
  private final ManyToOneField<Integer, Postcode> postcodeField;
  private final ManyToOneField<Short, LocationType> locTypeField;
  private final TextArea note;
  private final ToManyField<Long, Contact, Partner, List<Contact>> contacts;
  private final ToManyField<Long, PartnerFile, Partner, List<PartnerFile>> partnerFiles;
  private final ToManyField<Long, User, Partner, List<User>> users;

  private static final String NOTE_KEY
  = StringUtil.field(Partner_.PARTNER_DETAILS, PartnerDetails_.NOTE);
  private static final String MIDDLENAME_KEY
  = StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.MIDDLENAME);
  private static final String PASSPORT_SERIES_KEY
  = StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.PASSPORT_SERIES);
  private static final String PASSPORT_NUMBER_KEY
  = StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.PASSPORT_NUMBER);
  private static final String PASSPORT_DATE_KEY
  = StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.PASSPORT_DATE);
  private static final String PASSPORT_ISSUED_KEY
  = StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.PASSPORT_ISSUED);
  private static final String MARRIED_KEY
  = StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.MARRIED);
  private static final List<String> PERSON_FIELDS = List.of(
    MIDDLENAME_KEY, PASSPORT_SERIES_KEY, PASSPORT_NUMBER_KEY
  , PASSPORT_DATE_KEY, PASSPORT_ISSUED_KEY, MARRIED_KEY
  );

  public PartnerForm(
    SecurityMgr securityMgr
  , Validator validator
  , PartnersBasicDbGrid partnersBasicDbGrid
  , PostcodesDbGrid postcodesDbGrid
  , LocationTypesDbGrid locationTypesDbGrid
  , ContactGridFieldColumns contactColumns
  , ContactsDbGrid contactsDbGrid
  , PartnerFileGridFieldColumns partnerFileColumns
  , PartnerFilesDbGrid partnerFilesDbGrid
  , UserGridFieldColumns userColumns
  , UsersDbGrid usersDbGrid
  ) {
    super(Partner.TITLE
    , new EntityFormColumns(
        securityMgr
      , Partner.class
      , PartnerWor.class
      , new LinkedHashMap<String, Component>() {{
          ComboBox<PartnerType> partnerType
          = new ComboBox<>(Partner_.TYP, PartnerType.VALUES);
          partnerType.setValue(PartnerType.PERSON);
          partnerType.setItemLabelGenerator(Labeled::label);
          put(Partner_.TYP, partnerType);
          put(Partner_.PARTNER_NAME, new TextField());
          put(Partner_.PARTNER_FULLNAME, new TextField());
          put(MIDDLENAME_KEY, new TextField());
          put(Partner_.PARTNER_REGCODE, new TextField());
          put(Partner_.PARTNER_REGDATE, new DatePicker());
          put(Partner_.ADDRESS, new TextField());
          ManyToOneField<Long, Partner> parentField
          = new PartnerField(Partner_.PARENT, partnersBasicDbGrid);
          parentField.setClearButtonVisible(true);
          put(Partner_.PARENT, parentField);
          ManyToOneField<Integer, Postcode> postcodeField = new ManyToOneField<>(
            Partner_.POSTCODE
          , postcodesDbGrid
          , Postcode::getPostcode
          , Postcode::getPostcode
          , List.of(Postcode_.POSTCODE)
          );
          postcodeField.setClearButtonVisible(true);
          put(Partner_.POSTCODE, postcodeField);
          ManyToOneField<Short, LocationType> locTypeField = new ManyToOneField<>(
            Partner_.LOCATION_TYPE
          , locationTypesDbGrid
          , LocationType::getName
          , LocationType::getDescr
          , List.of(LocationType_.NAME, LocationType_.DESCR)
          );
          locTypeField.setClearButtonVisible(true);
          put(Partner_.LOCATION_TYPE, locTypeField);
          put(NOTE_KEY, new TextArea());
          put(PASSPORT_SERIES_KEY, new TextField());
          put(PASSPORT_NUMBER_KEY, new IntegerField());
          put(PASSPORT_DATE_KEY, new DatePicker());
          put(PASSPORT_ISSUED_KEY, new TextArea());
          put(MARRIED_KEY, new Checkbox());
          put(Partner_.CONTACTS, new ToManyField<>(
            Partner_.CONTACTS
          , Partner.class
          , ArrayList<Contact>::new
          , contactColumns
          , contactsDbGrid
          , securityMgr
          , false
          ));
          put(Partner_.PARTNER_FILES, new ToManyField<>(
            Partner_.PARTNER_FILES
          , Partner.class
          , ArrayList<PartnerFile>::new
          , partnerFileColumns
          , partnerFilesDbGrid
          , securityMgr
          , false
          ));
          put(Partner_.USERS, new ToManyField<>(
            Partner_.USERS
          , Partner.class
          , ArrayList<User>::new
          , userColumns
          , usersDbGrid
          , securityMgr
          , false
          ));
        }}
      )
    , validator
    );
    partnerType = (ComboBox<PartnerType>)columns.get(Partner_.TYP);
    partnerType.addValueChangeListener(e -> partnerTypeChange());
    name = (TextField)columns.get(Partner_.PARTNER_NAME);
    fullname = (TextField)columns.get(Partner_.PARTNER_FULLNAME);
    middlename = (TextField)columns.get(MIDDLENAME_KEY);
    regcode = (TextField)columns.get(Partner_.PARTNER_REGCODE);
    regdate = (DatePicker)columns.get(Partner_.PARTNER_REGDATE);
    passportSeries = (TextField)columns.get(PASSPORT_SERIES_KEY);
    passportNumber = (IntegerField)columns.get(PASSPORT_NUMBER_KEY);
    passportDate = (DatePicker)columns.get(PASSPORT_DATE_KEY);
    passportIssued = (TextArea)columns.get(PASSPORT_ISSUED_KEY);
    married = (Checkbox)columns.get(MARRIED_KEY);
    address = (TextField)columns.get(Partner_.ADDRESS);
    parentField = (ManyToOneField<Long, Partner>)columns.get(Partner_.PARENT);
    postcodeField
    = (ManyToOneField<Integer, Postcode>)columns.get(Partner_.POSTCODE);
    locTypeField
    = (ManyToOneField<Short, LocationType>)columns.get(Partner_.LOCATION_TYPE);
    note = (TextArea)columns.get(NOTE_KEY);
    contacts = (ToManyField<Long, Contact, Partner, List<Contact>>)
    columns.get(Partner_.CONTACTS);
    partnerFiles = (ToManyField<Long, PartnerFile, Partner, List<PartnerFile>>)
    columns.get(Partner_.PARTNER_FILES);
    users = (ToManyField<Long, User, Partner, List<User>>)
    columns.get(Partner_.USERS);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    partnerTypeChange();
  }

  private void partnerTypeChange() {
    PartnerType pt = partnerType.getValue();
    boolean isPerson = pt != null && pt.isPerson;
    middlename.setVisible(isPerson);
    passportSeries.setVisible(isPerson);
    passportNumber.setVisible(isPerson);
    passportDate.setVisible(isPerson);
    passportIssued.setVisible(isPerson);
    married.setVisible(isPerson);
    String nameKey, fullnameKey, regcodeKey, regdateKey;
    if(isPerson) {
      nameKey = Partner.namePerson;
      fullnameKey = Partner.fullnamePerson;
      regcodeKey = Partner.regcodePerson;
      regdateKey = Partner.regdatePerson;
    } else {
      nameKey = Partner.nameNotPerson;
      fullnameKey = Partner.fullnameNotPerson;
      regcodeKey = Partner.regcodeNotPerson;
      regdateKey = Partner.regdateNotPerson;
    }
    name.setLabel(getTranslation(nameKey));
    fullname.setLabel(getTranslation(fullnameKey));
    regcode.setLabel(getTranslation(regcodeKey));
    regdate.setLabel(getTranslation(regdateKey));
  }

  @Override
  protected boolean isValid(Partner data) {
    List<String> vf = new ArrayList<>(fields);
    if(StringUtils.isBlank(note.getValue())) {
      if(vf.contains(NOTE_KEY)) {
        vf.remove(NOTE_KEY);
      }
    } else {
      if(!vf.contains(NOTE_KEY)) {
        vf.add(NOTE_KEY);
      }
    }
    PartnerType pt = partnerType.getValue();
    boolean isPerson = pt != null && pt.isPerson;
    if(isPerson) {
      if(!vf.contains(MARRIED_KEY)) {
        vf.addAll(PERSON_FIELDS);
      }
    } else {
      if(vf.contains(MARRIED_KEY)) {
        vf.removeAll(PERSON_FIELDS);
      }
    }
    Set<ConstraintViolation<?>> validate = new HashSet<>();
    PartnerWor worData = new PartnerWor(data);
    ColumnDescr.validate(validator, validate, columns.classWor, worData, vf);
    return validate.isEmpty();
  }

  @Override
  protected Partner onSave(Partner data) {
    Partner result = super.onSave(data);
    if(StringUtils.isBlank(result.getPartnerDetails().getNote())) {
      result.getPartnerDetails().setNote(null);
    }
    PartnerType pt = result.getTyp();
    boolean isPerson = pt != null && pt.isPerson;
    if(!isPerson) {
      result.setPersonDetails(null);
    }
    return result;
  }

}
