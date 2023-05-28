package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.PaginationParametersException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    UserService userService;
    RequestJpaRepository requestRepository;
    ItemJpaRepository itemRepository;

    @Autowired
    public RequestServiceImpl(UserService userService, RequestJpaRepository requestRepository,
                              ItemJpaRepository itemRepository) {
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, int userId) {
        itemRequestDto.setRequestor(userService.getUserDtoById(userId)); //присваиваем владельца
        ItemRequest itemRequest = RequestMapper.dtoToRequest(itemRequestDto); //превращаем dto в объект
        requestRepository.save(itemRequest); //сохраняем запрос в хранилище
        return RequestMapper.requestToDto(itemRequest); //возвращаем DTO Объекта
    }

    @Override
    public List<ItemRequestDtoWithAnswer> getAllDtoWithAnswByUserId(int userId) {
        userService.getUserDtoById(userId); //запрашиваем пользователя из БД для проверки его существования. Если не сущ. - исключение

        List<ItemRequest> requestList = requestRepository.findAllByRequestor_Id(userId, Sort.by("created").descending()); //список запросов пользователя от новых к старым
        return setAnswersToRequests(requestList);
    }

    @Override
    public List<ItemRequestDtoWithAnswer> getAllDtoWithAnsw(int from, int size, int userId) {
        if (from < 0 || size < 1) {
            throw new PaginationParametersException("Параметры постраничной выбрки должны быть from >=0, size >0");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending()); //параметризируем переменную для пагинации
        List<ItemRequest> requestList = requestRepository.findAllByRequestor_IdNot(userId, page); //список запросов ДРУГИХ пользователя от новых к старым в рамках одной страницы
        return setAnswersToRequests(requestList);
    }

    @Override
    public ItemRequestDtoWithAnswer getDtoWithAnswById(int requestId, int userId) {
        userService.getUserDtoById(userId); //запрашиваем пользователя из БД для проверки его существования. Если не сущ. - исключение
        ItemRequest itemRequest = requestRepository.findById(requestId); //взяли из репозитория запрос по id
        if (itemRequest == null) {
            throw new ElementNotFoundException("Запрос с Id=" + requestId + " не найден");
        }
        List<ItemRequestDtoWithAnswer> requestWithAnswrList = setAnswersToRequests(Collections.singletonList(itemRequest)); //передали в метод список из одного запроса
        return requestWithAnswrList.get(0); //вернули единственный элемент из списка
    }


    /**
     * Преобразование списка запросов в список DTO с вложенными Item, созданными по этим запросам
     *
     * @param requestList - список запросов, в которые требуется встроить отве[ты.
     * @return - список запросов с ответами
     */
    private List<ItemRequestDtoWithAnswer> setAnswersToRequests(List<ItemRequest> requestList) {

        List<ItemDto> itemDtoList = new ArrayList<>(); //Список DTO вещей, созданных по запросу
        if (!requestList.isEmpty()) { // с пустым списком запросов не надо вызывать его преобразование в список id
            List<Integer> idList = requestList.stream() //список id запросов
                    .map(ItemRequest::getId)
                    .collect(Collectors.toList());
            List<Item> itemList = itemRepository.findByRequest_IdIn(idList);//список Item, содерж. нужные request_id
            itemDtoList = itemList.stream()
                    .map(ItemMapper::itemToDto).collect(Collectors.toList()); //преобразовали в ItemDto
        }
        Map<Integer, List<ItemDto>> answerMap = itemDtoList.stream() //преобразовали в мапу id запроса-список Itemов
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requestList.stream()//преобраовали список запросов в список ItemRequestDtoWithAnswer, с вложенными ответами из ItemDto
                .map(r -> RequestMapper.requestToDtoWithAnswer(r, answerMap.get(r.getId())))
                .collect(Collectors.toList());
    }
}
