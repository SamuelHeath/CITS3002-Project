from Crypto.PublicKey import RSA

secret_code = "Unguessable"
key = RSA.generate(2048)
encrypted_key = key.exportKey(passphrase=secret_code, pkcs=8, protection="scryptAndAES128-CBC")
file_out = open("rsa_key.bin", "wb")
file_out.write(encrypted_key)
file_out.close()

#print key.publickey().exportKey()

print "Key has been generated and will now be read\n"

encoded_key = open("rsa_key.bin", "rb").read()
key_read = RSA.import_key(encoded_key, passphrase=secret_code)

print key_read.publickey().exportKey()
print "\n"
print key_read.exportKey()