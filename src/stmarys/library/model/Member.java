package stmarys.library.model;

public class Member extends LibraryEntity {
    private String memberName;
    private String email;
    private String membershipType;

    public Member(int memberId, String memberName, String email, String membershipType) {
        super(memberId);
        this.memberName = memberName;
        this.email = email;
        this.membershipType = membershipType;
    }

    public int getMemberId() {
        return getId();
    }

    public void setMemberId(int memberId) {
        setId(memberId);
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }
}
