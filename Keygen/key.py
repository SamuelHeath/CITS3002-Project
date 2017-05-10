from Crypto.PublicKey import RSA
from Crypto.Hash import SHA256
from Crypto.Hash import RIPEMD160
import base58

secret_code = "Unguessable"
key = RSA.generate(2048)
encrypted_key = key.exportKey(passphrase=secret_code, pkcs=8, protection="scryptAndAES128-CBC")
file_out = open("rsa_key.bin", "wb")
file_out.write(encrypted_key)
file_out.close()

	#print key.publickey().exportKey()

	#print "Key has been generated and will now be read\n"

encoded_key = open("rsa_key.bin", "rb").read()
key_read = RSA.import_key(encoded_key, passphrase=secret_code)

	#print key_read.publickey().exportKey()
	#print "\n"
	#print key_read.exportKey()

#Encrypts public key with SHA256 hashing
public_sha256 = SHA256.new()
public_sha256.update(key_read.publickey().exportKey())
	#print public_sha256.hexdigest()
	#print "\n"

#Encrypts public_sha256 with RIPEMD-160 hashing
public_sha256_RIPEMD160 = RIPEMD160.new()
public_sha256_RIPEMD160.update(public_sha256.hexdigest())
	#print "Pre Version Byte:"
	#print public_sha256_RIPEMD160.hexdigest()

"""
Add version byte in front of public_sha256_RIPEMD160
	0x00 = Main Network
	0x6f = Test Network
	0x34 = Namcoin Net
"""

version_byte = "00" + public_sha256_RIPEMD160.hexdigest()
	#print "Version byte:"
	#print version_byte

#Base58Check Encoding starts here

#Perform SHA256 on the extended RIPEMD-160 result from before
extended_sha256 = SHA256.new()
extended_sha256.update(version_byte)
	#print extended_sha256.hexdigest()

#Perform SHA256 again. Double-hashing will prevent the risk of a length extension attack
double_hashed_sha256 = SHA256.new()
double_hashed_sha256.update(extended_sha256.hexdigest())
	#print double_hashed_sha256.hexdigest()

#Extracts the first 4 bytes of double_hashed_sha256
address_checksum = double_hashed_sha256.hexdigest()[:8]
	#print address_checksum

#Adds the 4 checksum bytes from address_checksum at the end of version_byte
binary_bitcoin_address = version_byte + address_checksum
	#print binary_pre_bitcoin_address

#Converts binary_pre_bitcoin_address into a base58 string using Base58Check encoding.
byte_encoding = str(bytearray.fromhex( binary_bitcoin_address ))
bitcoin_address = base58.b58encode(byte_encoding)

print "Your Bitcoin address is:"
print bitcoin_address












