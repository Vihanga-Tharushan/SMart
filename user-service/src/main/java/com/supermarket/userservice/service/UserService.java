package com.supermarket.userservice.service;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.supermarket.userservice.dto.UserDTO;
import com.supermarket.userservice.model.User;
import com.supermarket.userservice.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<UserDTO> getAllUsers(){
        List<User>userList = userRepository.findAll();
        return modelMapper.map(userList, new TypeToken<List<UserDTO>>(){}.getType());
    }

    public UserDTO saveUser(UserDTO userDTO){

        User user = modelMapper.map(userDTO, User.class);
        user.setId(null);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO updateUser(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    public String deleteUser(long userId){

        userRepository.deleteById(userId);
        return "User Deleted Successfully";
    }

    public UserDTO getUserById(long userId){

        User user = userRepository.getUserById(userId);
        return modelMapper.map(user, UserDTO.class);
    }


}
