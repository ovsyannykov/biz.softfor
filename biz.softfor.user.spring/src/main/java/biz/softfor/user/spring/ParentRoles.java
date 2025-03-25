package biz.softfor.user.spring;

public record ParentRoles(Long host, Long type) {

  public ParentRoles(long host) {
    this(host, null);
  }

}
