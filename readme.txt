Kullanıcı user bilgilerini post ederek token ve refresh token almaktadır. Kullanıcıya response edilen refresh token, access token 
düşmesi durumunda tekrardan token alması için kullanılmaktadır. Client tarafında tutulan refresh token, yazılan kontrol mekanizmasına 
göre server tarafına request edilir ve access token yenilenir. Bu mekanizma sayesinde kullanıcı access token süresinden bağımsız 
olarak işlemlerine devam etmektedir. 

Projede access token security configurasyon, filter ve user servisleri üzerinden alınmaktadır. Access token alınırken paralel olarak
üretilen refresh token mongodbde tutulmaktadır. Auth içinde kullanılan refresh token servisi ile token alınmaktadır. 