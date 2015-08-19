package yappse.wallet;



class User
{
      User() { }

      String get_id() {
        return _id;
    }

      void set_id(String _id) {
        this._id = _id;
    }

      String getAccountId() {
        return accountId;
    }

      void setAccountId(String accountId) {
        this.accountId = accountId;
    }

      String getRefererId() {
        return refererId;
    }

      void setRefererId(String refererId) {
        this.refererId = refererId;
    }

      String getUsername() {
        return username;
    }

      void setUsername(String username) {
        this.username = username;
    }

      String getAvatar() {
        return avatar;
    }

      void setAvatar(String avatar) {
        this.avatar = avatar;
    }

      String getFullname() {
        return fullname;
    }

      void setFullname(String fullname) {
        this.fullname = fullname;
    }

      String getEmail() {
        return email;
    }

      void setEmail(String email) {
        this.email = email;
    }

      String getCreatedAt() {
        return createdAt;
    }

      void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

      String getIdentify() {
        return identify;
    }

      void setIdentify(String identify) {
        this.identify = identify;
    }

      String getBalanceEur() {
        return balanceEur;
    }

      void setBalanceEur(String balanceEur) {
        this.balanceEur = balanceEur;
    }

      String getBalanceTokens() {
        return balanceTokens;
    }

      void setBalanceTokens(String balanceTokens) {
        this.balanceTokens = balanceTokens;
    }

      String getBalanceCoins() {
        return balanceCoins;
    }

      void setBalanceCoins(String balanceCoins) {
        this.balanceCoins = balanceCoins;
    }

      String getStatus() {
        return status;
    }

      void setStatus(String status) {
        this.status = status;
    }

      String getLevel() {
        return level;
    }

      void setLevel(String level) {
        this.level = level;
    }

      String get_package() {
        return _package;
    }

      void set_package(String _package) {
        this._package = _package;
    }

      String getBio() {
        return bio;
    }

      void setBio(String bio) {
        this.bio = bio;
    }

      String getDob() {
        return dob;
    }

      void setDob(String dob) {
        this.dob = dob;
    }

      String getGender() {
        return gender;
    }

      void setGender(String gender) {
        this.gender = gender;
    }

      String getCountry() {
        return country;
    }

      void setCountry(String country) {
        this.country = country;
    }

      String getCity() {
        return city;
    }

      void setCity(String city) {
        this.city = city;
    }

      String getZip() {
        return zip;
    }

      void setZip(String zip) {
        this.zip = zip;
    }

      String getAddress() {
        return address;
    }

      void setAddress(String address) {
        this.address = address;
    }

      String getPhone() {
        return phone;
    }

      void setPhone(String phone) {
        this.phone = phone;
    }

      String getActive() {
        return active;
    }

      void setActive(String active) {
        this.active = active;
    }

      String getUpdatedAt() {
        return updatedAt;
    }

      void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

      String getCrxunits() {
        return crxunits;
    }

      void setCrxunits(String crxunits) {
        this.crxunits = crxunits;
    }

      String getCoinsEscrow() {
        return coinsEscrow;
    }

      void setCoinsEscrow(String coinsEscrow) {
        this.coinsEscrow = coinsEscrow;
    }

      String getTokensAvailable() {
        return tokensAvailable;
    }

      void setTokensAvailable(String tokensAvailable) {
        this.tokensAvailable = tokensAvailable;
    }

      String getTokensEscrow() {
        return tokensEscrow;
    }

      void setTokensEscrow(String tokensEscrow) {
        this.tokensEscrow = tokensEscrow;
    }

      String _id;
      String accountId;
      String refererId;
      String username;
      String avatar;
      String fullname;
      String email;
      String createdAt;
      String identify;
      String balanceEur;
      String balanceTokens;
      String balanceCoins;
      String status;
      String level;
      String _package;
      String bio;
      String dob;
      String gender;
      String country;
      String city;
      String zip;
      String address;
      String phone;
      String active;
      String updatedAt;
      String crxunits;
      String coinsEscrow;
      String tokensAvailable;
      String tokensEscrow;
}

  class Transaction
{
      Transaction() { }
      Transaction(String n_transactionID, String n_type, String n_createdAt, String n_status, String n_amountMCO)
    {
        transactionId = n_transactionID;
        type = n_type;
        createdAt = n_createdAt;
        status = n_status;
        amountMCO = n_amountMCO;
    }

    public String getTransactionID() {
        return transactionId;
    }

    public void setTransactionID(String transactionID) {
        this.transactionId = transactionID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmountMCO() {
        return amountMCO;
    }

    public void setAmountMCO(String amountMCO) {
        this.amountMCO = amountMCO;
    }

    String transactionId;
      String type;
      String createdAt;
      String status;
      String amountMCO;
}
  class Authentication
{
      Authentication() { }
      Authentication(boolean n_authenticated, String n_secret)
    {
        authenticated = n_authenticated;
        secret = n_secret;
    }
      boolean authenticated;
      String secret;
}
  class SendMCOResponse
{
      SendMCOResponse() { }
      SendMCOResponse(String n_status, String n_transactionId, String n_timestamp, String n_message)
    {
        status = n_status;
        transactionId = n_transactionId;
        timestamp = n_timestamp;
        message = n_message;
    }
      String status;
      String transactionId;
      String timestamp;
      String message;
}
  class Balance
{
      Balance() { }
      Balance(String n_accountId, String n_balanceCoin, String n_balanceCoinEscrow, String n_balanceEur, String n_balanceEurEscrow)
    {
        accountId = n_accountId;
        balanceCoin = n_balanceCoin;
        balanceCoinEscrow = n_balanceCoinEscrow;
        balanceEur = n_balanceEur;
        balanceEurEscrow = n_balanceEurEscrow;
    }
      String accountId;
      String balanceCoin;
      String balanceCoinEscrow;
      String balanceEur;
      String balanceEurEscrow;
}
  class ExchangeResponse
{
      ExchangeResponse() { }
      ExchangeResponse(String n_status, String n_transactionId, String n_createdAt)
    {
        status = n_status;
        transactionId = n_transactionId;
        createdAt = n_transactionId;
    }
      String status;
      String transactionId;
      String createdAt;
}
class Rate {

    String rate, date;

    public Rate() { }

    public String getRate(){ return this.rate;}
    public void setRate(String rate) {this.rate = rate;}

    public String getDate(){ return this.date;}
    public void setDate(String date) {this.date = date;}

}

  class UpdateProfileResponse
{
      UpdateProfileResponse(){ }
      boolean updated ;
}
  class ChangePasswordResponse
{
      ChangePasswordResponse(){ }
      boolean updated ;
}
  class ForgottenPasswordResponse
{
      ForgottenPasswordResponse(){ }
      boolean updated ;
}
