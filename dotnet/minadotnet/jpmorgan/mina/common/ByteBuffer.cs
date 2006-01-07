using System;
using System.Text;

namespace jpmorgan.mina.common
{
    /// <summary>
    /// A buffer that manages an underlying byte oriented stream, and writes and reads to and from it in
    /// BIG ENDIAN order.
    /// </summary>
    public class ByteBuffer
    {
        private byte[] _underlyingData;

        /// <summary>
        /// The position of the next value to be read or written
        /// </summary>
        private int _position;

        /// <summary>
        /// The index of the first element that should not be read or written
        /// </summary>
        private int _limit;

        public ByteBuffer(int size)
        {
            _underlyingData = new byte[size];
        }

        public static ByteBuffer allocate(int size)
        {
            // naive implementation for now
            return new ByteBuffer(size);
        }

        public static ByteBuffer allocate(uint size)
        {
            // naive implementation for now
            return new ByteBuffer((int)size);
        }

        public int Capacity
        {
            get
            {
                return _underlyingData.Length;
            }
        }

        public int Position
        {
            get
            {
                return _position;
            }
            set
            {
                _position = value;
            }
        }

        /// <summary>
        /// Sets this buffer's limit. If the position is larger than the new limit then it is set to the new limit.
        /// </summary>
        /// <value>The new limit value; must be non-negative and no larger than this buffer's capacity</value>
        public int Limit
        {
            get
            {
                return _limit;
            }
            set
            {
                if (value < 0)
                {
                    throw new ArgumentException("Limit must not be negative");
                }
                if (value > Capacity)
                {
                    throw new ArgumentException("Limit must not be greater than Capacity");
                }
                _limit = value;
                if (_position > value)
                {
                    _position = value;
                }
            }
        }

        /// <summary>
        /// Returns the number of elements between the current position and the limit
        /// </summary>
        /// <value>The number of elements remaining in this buffer</value>
        public int Remaining
        {
            get
            {
                return (_limit - _position);
            }
        }

        public void Clear()
        {
            _position = 0;
            _limit = Capacity;
        }

        public void Flip()
        {
            _limit = _position;
            _position = 0;
        }

        public void Rewind()
        {
            _position = 0;
        }

        public byte[] Buffer
        {
            get
            {
                return _underlyingData;
            }
        }

        private void CheckSpace(int size)
        {
            if (_position + size > _limit)
            {
                throw new BufferOverflowException("Attempt to write " + size + " byte(s) to buffer where position is " + _position +
                                                  " and limit is " + _limit);
            }
        }

        private void CheckSpaceForReading(int size)
        {
            if (_position + size > _limit)
            {
                throw new BufferUnderflowException("Attempt to read " + size + " byte(s) to buffer where position is " + _position +
                                                   " and limit is " + _limit);
            }
        }

        /// <summary>
        /// Writes the given byte into this buffer at the current position, and then increments the position.
        /// </summary>
        /// <param name="data">The byte to be written</param>
        /// <exception cref="BufferOverflowException">If this buffer's current position is not smaller than its limit</exception>
        public void Put(byte data)
        {
            CheckSpace(1);
            _underlyingData[_position++] = data;
        }

        /// <summary>
        /// Writes all the data in the given byte array into this buffer at the current
        /// position and then increments the position.
        /// </summary>
        /// <param name="data">The data to copy.</param>
        /// <exception cref="BufferOverflowException">If this buffer's current position plus the array length is not smaller than its limit</exception>
        public void Put(byte[] data)
        {
            if (data == null)
            {
                throw new ArgumentNullException("data");
            }
            CheckSpace(data.Length);
            System.Array.Copy(data, 0, _underlyingData, _position, data.Length);
            _position += data.Length;
        }

        /// <summary>
        /// Writes the given ushort into this buffer at the current position, and then increments the position.
        /// </summary>
        /// <param name="data">The ushort to be written</param>
        public void Put(ushort data)
        {
            CheckSpace(2);
            _underlyingData[_position++] = (byte) (data >> 8);
            _underlyingData[_position++] = (byte) data;
        }

        public void Put(uint data)
        {
            CheckSpace(4);
            _underlyingData[_position++] = (byte) (data >> 24);
            _underlyingData[_position++] = (byte) (data >> 16);
            _underlyingData[_position++] = (byte) (data >> 8);
            _underlyingData[_position++] = (byte) data;
        }

        public void Put(ulong data)
        {
            CheckSpace(8);
            _underlyingData[_position++] = (byte) (data >> 56);
            _underlyingData[_position++] = (byte) (data >> 48);
            _underlyingData[_position++] = (byte) (data >> 40);
            _underlyingData[_position++] = (byte) (data >> 32);
            _underlyingData[_position++] = (byte) (data >> 24);
            _underlyingData[_position++] = (byte) (data >> 16);
            _underlyingData[_position++] = (byte) (data >> 8);
            _underlyingData[_position++] = (byte) data;
        }

        /// <summary>
        /// Read the byte at the current position and increment the position
        /// </summary>
        /// <returns>a byte</returns>
        /// <exception cref="BufferUnderflowException">if there are no bytes left to read</exception>
        public byte Get()
        {
            CheckSpaceForReading(1);
            return _underlyingData[_position++];
        }

        /// <summary>
        /// Reads bytes from the buffer into the supplied array
        /// </summary>
        /// <param name="destination">The destination array. The array must not
        /// be bigger than the remaining space in the buffer, nor can it be null.</param>
        public void Get(byte[] destination)
        {
            if (destination == null)
            {
                throw new ArgumentNullException("destination");
            }
            int len = destination.Length;
            CheckSpaceForReading(len);
            System.Array.Copy(_underlyingData, _position, destination, 0, len);
            _position += len;
        }

        /// <summary>
        /// Reads and returns an unsigned short (two bytes, big endian) from this buffer
        /// </summary>
        /// <returns>an unsigned short</returns>
        /// <exception cref="BufferUnderflowException">If there are fewer than two bytes remaining in this buffer</exception>
        public ushort GetUnsignedShort()
        {
            CheckSpaceForReading(2);
            byte upper = _underlyingData[_position++];
            byte lower = _underlyingData[_position++];
            return (ushort) ((upper << 8) + lower);
        }

        /// <summary>
        /// Reads and returns an unsigned int (four bytes, big endian) from this buffer
        /// </summary>
        /// <returns>an unsigned integer</returns>
        /// <exception cref="BufferUnderflowException">If there are fewer than four bytes remaining in this buffer</exception>
        public uint GetUnsignedInt()
        {
            CheckSpaceForReading(4);
            byte b1 = _underlyingData[_position++];
            byte b2 = _underlyingData[_position++];
            byte b3 = _underlyingData[_position++];
            byte b4 = _underlyingData[_position++];
            return (uint) ((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
        }

        public ulong GetUnsignedLong()
        {
            CheckSpaceForReading(8);
            byte b1 = _underlyingData[_position++];
            byte b2 = _underlyingData[_position++];
            byte b3 = _underlyingData[_position++];
            byte b4 = _underlyingData[_position++];
            byte b5 = _underlyingData[_position++];
            byte b6 = _underlyingData[_position++];
            byte b7 = _underlyingData[_position++];
            byte b8 = _underlyingData[_position++];
            return (ulong)((b1 << 56) + (b2 << 48) + (b3 << 40) + (b4 << 32) + (b5 << 24) +
                   (b6 << 16) + (b7 << 8) + b8);
        }

        public string GetString(uint length, Encoding encoder)
        {
            CheckSpaceForReading((int)length);
            string result = encoder.GetString(_underlyingData, _position, (int)length);
            _position += (int)length;
            return result;
        }
    }
}